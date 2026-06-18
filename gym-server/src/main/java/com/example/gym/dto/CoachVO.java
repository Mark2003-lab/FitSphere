package com.example.gym.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoachVO {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String speciality;
    private String description;
    private BigDecimal price;
    private BigDecimal rating;
    private Integer totalReviews;
    private String avatar;
    private String certification;
    private Integer experienceYears;
}
