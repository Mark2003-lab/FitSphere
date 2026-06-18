package com.example.gym.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseCheckinVO {
    private Long reservationId;
    private Long checkinId;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private Long courseId;
    private String courseName;
    private LocalDateTime courseTime;
    private String reservationStatus;
    private String paymentStatus;
    private String checkinStatus;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
}
