package com.example.gym.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String role;
    private String email;
    private String code; // 验证码
}
