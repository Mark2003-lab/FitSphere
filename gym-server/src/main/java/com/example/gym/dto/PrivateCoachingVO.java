package com.example.gym.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PrivateCoachingVO {
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private Long coachId;
    private String coachName;
    private String coachAvatar;
    private String coachSpeciality;
    private String courseName;
    private String courseType;
    private LocalDateTime scheduledTime;
    private Integer duration;
    private BigDecimal price;
    private String status;
    private String paymentStatus;
    private Long couponId;
    private String location;
    private String notes;
    private String coachNotes;
    private Integer rating;
    private String review;
    private LocalDateTime createTime;
    private LocalDateTime auditTime;
    private LocalDateTime completeTime;

    /**
     * 状态中文映射
     */
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case "PENDING":   return "待审核";
            case "APPROVED":  return "已通过";
            case "REJECTED":  return "已拒绝";
            case "CONFIRMED": return "已确认";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default:          return status;
        }
    }

    /**
     * 支付状态中文映射
     */
    public String getPaymentStatusText() {
        if (paymentStatus == null) return "";
        switch (paymentStatus) {
            case "UNPAID": return "未支付";
            case "PAID":   return "已支付";
            default:       return paymentStatus;
        }
    }
}
