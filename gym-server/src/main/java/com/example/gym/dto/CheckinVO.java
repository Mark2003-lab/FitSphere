package com.example.gym.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到记录视图对象,包含关联的会员、课程、教练信息
 */
@Data
public class CheckinVO {
    
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private String status;
    private String type;
    private Long courseId;
    private String courseName;
    private Long coachId;
    private String coachName;
    private String remark;
    
    /** 停留时长(分钟) */
    private Long duration;
    
    /** 预约状态 */
    private String reservationStatus;
    
    /** 支付状态 */
    private String paymentStatus;
    
    /** 签到状态 */
    private String checkinStatus;
    
    /** 课程时间 */
    private LocalDateTime courseTime;
}
