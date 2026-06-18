package com.example.gym.dto;

import lombok.Data;

/**
 * 课程签到DTO
 */
@Data
public class CourseCheckinDTO {
    
    /** 课程ID */
    private Long courseId;
    
    /** 签到持续时长（分钟） */
    private Integer durationMinutes;
}