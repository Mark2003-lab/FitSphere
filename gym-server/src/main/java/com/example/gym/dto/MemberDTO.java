package com.example.gym.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberDTO {
    private String name;
    private String gender;
    private String phone;
    private String level;
    private LocalDateTime expireTime;
}
