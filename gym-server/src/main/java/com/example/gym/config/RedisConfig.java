package com.example.gym.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;

/**
 * Redis配置类
 * 配置RedisTemplate和CacheManager
 * 当Redis不可用时，自动回退到内存缓存
 */
@Configuration
@EnableCaching
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 创建ObjectMapper并配置
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册Java 8日期时间模块
        objectMapper.registerModule(new JavaTimeModule());
        
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 指定序列化输入的类型，类必须是非final修饰的
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        
        return objectMapper;
    }

    /**
     * 配置RedisTemplate
     * 使用Jackson序列化器
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(createObjectMapper());

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis缓存管理器
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        try {
            // 测试Redis连接
            connectionFactory.getConnection().ping();
            
            // 配置序列化
            Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
            jackson2JsonRedisSerializer.setObjectMapper(createObjectMapper());

            // 配置缓存
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))  // 默认缓存1小时
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();  // 不缓存null值

            logger.info("Redis缓存管理器初始化成功");
            return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
        } catch (Exception e) {
            logger.warn("Redis连接失败，将回退到内存缓存: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 内存缓存管理器（作为Redis的回退）
     */
    @Bean
    public CacheManager simpleCacheManager() {
        logger.info("内存缓存管理器初始化");
        return new ConcurrentMapCacheManager();
    }

    /**
     * 复合缓存管理器
     * 优先使用Redis缓存，当Redis不可用时回退到内存缓存
     */
    @Bean
    @Primary
    public CacheManager compositeCacheManager(CacheManager redisCacheManager, CacheManager simpleCacheManager) {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        
        if (redisCacheManager != null) {
            compositeCacheManager.setCacheManagers(Arrays.asList(redisCacheManager, simpleCacheManager));
            logger.info("使用复合缓存管理器: Redis + 内存缓存");
        } else {
            compositeCacheManager.setCacheManagers(Arrays.asList(simpleCacheManager));
            logger.warn("Redis不可用，仅使用内存缓存");
        }
        
        // 设置为true时，如果所有缓存管理器都找不到缓存，则返回null
        compositeCacheManager.setFallbackToNoOpCache(false);
        return compositeCacheManager;
    }
}