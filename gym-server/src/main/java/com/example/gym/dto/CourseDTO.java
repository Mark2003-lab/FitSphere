package com.example.gym.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDTO {
    private String courseName;
    private Long coachId;
    private LocalDateTime courseTime;
    private Integer capacity;
    private String location;
    private java.math.BigDecimal price;
    private String status;
    private String auditStatus;
}