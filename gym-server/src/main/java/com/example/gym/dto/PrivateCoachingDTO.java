package com.example.gym.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PrivateCoachingDTO {
    private Long coachId;
    private String courseName;
    private String courseType;
    private LocalDateTime scheduledTime;
    private Integer duration;
    private String location;
    private String notes;
}
