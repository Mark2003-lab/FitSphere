package com.example.gym;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt 密码哈希生成器
 * 直接运行此文件即可生成密码哈希
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=========================================");
        System.out.println("  BCrypt 密码哈希生成器");
        System.out.println("=========================================\n");
        
        // 生成 admin 密码的哈希
        String adminHash = encoder.encode("admin");
        System.out.println("密码: admin");
        System.out.println("哈希值: " + adminHash);
        System.out.println("\nSQL 语句:");
        System.out.println("UPDATE user SET password = '" + adminHash + "' WHERE username = 'admin';\n");
        
        // 生成 123456 密码的哈希
        String passwordHash = encoder.encode("123456");
        System.out.println("密码: 123456");
        System.out.println("哈希值: " + passwordHash);
        System.out.println("\nSQL 语句:");
        System.out.println("UPDATE user SET password = '" + passwordHash + "' WHERE username = 'coach1';");
        System.out.println("UPDATE user SET password = '" + passwordHash + "' WHERE username = 'member1';\n");
        
        System.out.println("=========================================");
        System.out.println("复制上面的 SQL 语句到数据库执行即可!");
        System.out.println("=========================================");
    }
}
