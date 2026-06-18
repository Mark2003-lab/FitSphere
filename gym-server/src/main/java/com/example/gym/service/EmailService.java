package com.example.gym.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @param code 验证码
     */
    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("智能健身房管理系统 - 注册验证码");
            
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #667eea;">智能健身房管理系统</h2>
                    <p>您好!</p>
                    <p>您正在注册智能健身房管理系统账号,请使用以下验证码完成注册:</p>
                    <div style="background: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px;">
                        <h1 style="color: #667eea; letter-spacing: 5px; margin: 0;">%s</h1>
                    </div>
                    <p style="color: #999; font-size: 12px;">验证码有效期为5分钟,请尽快使用。</p>
                    <p style="color: #999; font-size: 12px;">如果这不是您的操作,请忽略此邮件。</p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #999; font-size: 12px;">此邮件由系统自动发送,请勿回复。</p>
                </div>
                """.formatted(code);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("验证码邮件发送成功: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("发送验证码邮件失败: {}", toEmail, e);
            throw new RuntimeException("发送邮件失败,请稍后重试");
        }
    }
    
    /**
     * 发送密码重置成功邮件
     * @param toEmail 收件人邮箱
     * @param username 用户名
     * @param newPassword 新密码
     */
    public void sendPasswordResetEmail(String toEmail, String username, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("智能健身房管理系统 - 密码重置成功");
            
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #667eea;">智能健身房管理系统</h2>
                    <p>尊敬的 <strong>%s</strong>,您好!</p>
                    <p>您的密码已成功重置。</p>
                    <div style="background: #f5f5f5; padding: 20px; margin: 20px 0; border-radius: 5px;">
                        <p style="margin: 5px 0;"><strong>新密码：</strong><span style="color: #667eea; font-size: 18px;">%s</span></p>
                    </div>
                    <p style="color: #ff6b6b; font-weight: bold;">请立即登录系统并修改密码！</p>
                    <p style="color: #999; font-size: 12px;">如果这不是您的操作,请立即联系管理员。</p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #999; font-size: 12px;">此邮件由系统自动发送,请勿回复。</p>
                </div>
                """.formatted(username, newPassword);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("密码重置成功邮件发送成功: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("发送密码重置成功邮件失败: {}", toEmail, e);
            throw new RuntimeException("发送邮件失败,请稍后重试");
        }
    }
}
