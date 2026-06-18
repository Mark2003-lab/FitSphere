package com.example.gym.service;

import com.example.gym.dto.LoginDTO;
import com.example.gym.dto.RegisterDTO;
import com.example.gym.dto.ResetPasswordDTO;
import com.example.gym.entity.User;

public interface UserService {
    User register(RegisterDTO registerDTO);
    String login(LoginDTO loginDTO);
    User findByUsername(String username);
    
    /**
     * 发送注册验证码
     * @param email 邮箱地址
     */
    void sendVerificationCode(String email);
    
    /**
     * 发送重置密码验证码
     * @param email 邮箱地址
     */
    void sendResetPasswordCode(String email);
    
    /**
     * 重置密码
     * @param resetPasswordDTO 重置密码请求
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
    
    /**
     * 根据ID查找用户
     */
    User findById(Long id);
    
    /**
     * 更新用户信息
     */
    User update(User user);
    
    /**
     * 修改密码
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 解锁账号（管理员使用）
     * @param username 要解锁的用户名
     * @return 是否解锁成功
     */
    boolean unlockAccount(String username);
    
    /**
     * 获取账号登录安全信息
     * @param username 用户名
     * @return 安全信息（是否锁定、剩余尝试次数等）
     */
    LoginSecurityInfo getLoginSecurityInfo(String username);
    
    /**
     * 登录安全信息内部类
     */
    class LoginSecurityInfo {
        private boolean locked;
        private int remainingAttempts;
        private Long remainingLockSeconds;
        
        public LoginSecurityInfo(boolean locked, int remainingAttempts, Long remainingLockSeconds) {
            this.locked = locked;
            this.remainingAttempts = remainingAttempts;
            this.remainingLockSeconds = remainingLockSeconds;
        }
        
        public boolean isLocked() { return locked; }
        public void setLocked(boolean locked) { this.locked = locked; }
        public int getRemainingAttempts() { return remainingAttempts; }
        public void setRemainingAttempts(int remainingAttempts) { this.remainingAttempts = remainingAttempts; }
        public Long getRemainingLockSeconds() { return remainingLockSeconds; }
        public void setRemainingLockSeconds(Long remainingLockSeconds) { this.remainingLockSeconds = remainingLockSeconds; }
    }
}
