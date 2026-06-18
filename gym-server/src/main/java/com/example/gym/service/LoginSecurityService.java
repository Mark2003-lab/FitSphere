package com.example.gym.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录安全服务
 * 管理密码错误次数和账号锁定
 */
@Slf4j
@Service
public class LoginSecurityService {

    // 最大错误次数
    private static final int MAX_ERROR_COUNT = 5;
    
    // 锁定时间（分钟）
    private static final int LOCK_MINUTES = 5;
    
    // Redis Key前缀
    private static final String KEY_PREFIX = "login:error:";
    private static final String LOCK_KEY_PREFIX = "login:lock:";

    private final StringRedisTemplate redisTemplate;

    public LoginSecurityService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检查账号是否被锁定
     */
    public boolean isLocked(String username) {
        try {
            String lockKey = LOCK_KEY_PREFIX + username;
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        } catch (Exception e) {
            log.warn("Redis检查锁定状态失败，使用内存存储: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取剩余解锁时间（秒）
     */
    public long getRemainingLockSeconds(String username) {
        try {
            String lockKey = LOCK_KEY_PREFIX + username;
            Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
            return ttl != null && ttl > 0 ? ttl : 0;
        } catch (Exception e) {
            log.warn("Redis获取剩余时间失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 记录密码错误
     * @return 剩余尝试次数
     */
    public int recordError(String username) {
        try {
            String errorKey = KEY_PREFIX + username;
            String countStr = redisTemplate.opsForValue().get(errorKey);
            int count = countStr != null ? Integer.parseInt(countStr) : 0;
            count++;
            
            if (count >= MAX_ERROR_COUNT) {
                // 达到最大错误次数，锁定账号
                String lockKey = LOCK_KEY_PREFIX + username;
                redisTemplate.opsForValue().set(lockKey, "locked", LOCK_MINUTES, TimeUnit.MINUTES);
                redisTemplate.delete(errorKey);
                log.warn("账号 {} 因密码错误 {} 次被锁定{}分钟", username, MAX_ERROR_COUNT, LOCK_MINUTES);
                return 0;
            } else {
                // 记录错误次数，设置过期时间
                redisTemplate.opsForValue().set(errorKey, String.valueOf(count), LOCK_MINUTES, TimeUnit.MINUTES);
                return MAX_ERROR_COUNT - count;
            }
        } catch (Exception e) {
            log.warn("Redis记录错误次数失败: {}", e.getMessage());
            return MAX_ERROR_COUNT - 1;
        }
    }

    /**
     * 获取剩余尝试次数
     */
    public int getRemainingAttempts(String username) {
        try {
            String errorKey = KEY_PREFIX + username;
            String countStr = redisTemplate.opsForValue().get(errorKey);
            if (countStr == null) {
                return MAX_ERROR_COUNT;
            }
            int count = Integer.parseInt(countStr);
            return Math.max(0, MAX_ERROR_COUNT - count);
        } catch (Exception e) {
            log.warn("Redis获取剩余次数失败: {}", e.getMessage());
            return MAX_ERROR_COUNT;
        }
    }

    /**
     * 登录成功后清除错误记录
     */
    public void clearError(String username) {
        try {
            String errorKey = KEY_PREFIX + username;
            redisTemplate.delete(errorKey);
        } catch (Exception e) {
            log.warn("Redis清除错误记录失败: {}", e.getMessage());
        }
    }

    /**
     * 管理员解锁账号
     */
    public boolean unlock(String username) {
        try {
            String errorKey = KEY_PREFIX + username;
            String lockKey = LOCK_KEY_PREFIX + username;
            redisTemplate.delete(errorKey);
            redisTemplate.delete(lockKey);
            log.info("管理员解锁账号: {}", username);
            return true;
        } catch (Exception e) {
            log.warn("Redis解锁失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取最大错误次数
     */
    public int getMaxErrorCount() {
        return MAX_ERROR_COUNT;
    }

    /**
     * 获取锁定时间（分钟）
     */
    public int getLockMinutes() {
        return LOCK_MINUTES;
    }
}
