package com.example.gym.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务
 * 使用内存存储验证码,避免频繁数据库操作
 */
@Slf4j
@Service
public class VerificationCodeService {
    
    // 验证码类型枚举
    public enum CodeType {
        REGISTER,    // 注册验证码
        RESET_PASSWORD // 重置密码验证码
    }
    
    // 存储验证码:key为"邮箱_类型",value为验证码信息
    private final Map<String, CodeInfo> codeCache = new ConcurrentHashMap<>();
    
    // 验证码有效期(分钟)
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    /**
     * 生成并存储验证码
     * @param email 邮箱地址
     * @param type 验证码类型
     * @return 验证码
     */
    public String generateCode(String email, CodeType type) {
        String key = buildKey(email, type);
        
        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 存储验证码和过期时间
        codeCache.put(key, new CodeInfo(code, LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES)));
        
        log.info("生成验证码: email={}, type={}, code={}", email, type, code);
        return code;
    }
    
    /**
     * 验证验证码
     * @param email 邮箱地址
     * @param type 验证码类型
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String email, CodeType type, String code) {
        String key = buildKey(email, type);
        CodeInfo codeInfo = codeCache.get(key);
        
        if (codeInfo == null) {
            log.warn("验证码不存在: email={}, type={}", email, type);
            return false;
        }
        
        // 检查是否过期
        if (LocalDateTime.now().isAfter(codeInfo.getExpireTime())) {
            codeCache.remove(key);
            log.warn("验证码已过期: email={}, type={}", email, type);
            return false;
        }
        
        // 验证验证码是否正确
        boolean isValid = codeInfo.getCode().equals(code);
        if (isValid) {
            // 验证成功后删除验证码(防止重复使用)
            codeCache.remove(key);
            log.info("验证码验证成功: email={}, type={}", email, type);
        } else {
            log.warn("验证码错误: email={}, type={}, inputCode={}", email, type, code);
        }
        
        return isValid;
    }
    
    /**
     * 检查验证码是否存在
     * @param email 邮箱地址
     * @param type 验证码类型
     * @return 是否存在
     */
    public boolean hasCode(String email, CodeType type) {
        String key = buildKey(email, type);
        CodeInfo codeInfo = codeCache.get(key);
        if (codeInfo == null) {
            return false;
        }
        
        // 如果已过期,清除并返回false
        if (LocalDateTime.now().isAfter(codeInfo.getExpireTime())) {
            codeCache.remove(key);
            return false;
        }
        
        return true;
    }
    
    /**
     * 构建验证码缓存key
     */
    private String buildKey(String email, CodeType type) {
        return email + "_" + type.name();
    }
    
    /**
     * 验证码信息内部类
     */
    private static class CodeInfo {
        private final String code;
        private final LocalDateTime expireTime;
        
        public CodeInfo(String code, LocalDateTime expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
        
        public String getCode() {
            return code;
        }
        
        public LocalDateTime getExpireTime() {
            return expireTime;
        }
    }
}
