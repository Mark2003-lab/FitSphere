package com.example.gym.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    private String email;
    private String code; // 验证码
}
