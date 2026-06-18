package com.example.gym.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教练端课程签到视图对象
 * 合并预约信息和签到状态，用于教练查看课程会员出勤情况
 */
@Data
public class CoachCheckinVO {
    
    private Long courseId;
    private String courseName;
    private LocalDateTime courseTime;
    
    private Long memberId;
    private String memberName;
    private String memberPhone;
    
    /** 预约状态 */
    private String reservationStatus;
    /** 支付状态 */
    private String paymentStatus;
    /** 签到状态: CHECKED_IN / null */
    private String checkinStatus;
    /** 签到时间 */
    private LocalDateTime checkinTime;
}
