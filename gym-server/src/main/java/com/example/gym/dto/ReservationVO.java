package com.example.gym.dto;

import com.example.gym.entity.Reservation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约视图对象，包含关联信息
 */
@Data
public class ReservationVO {
    
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private Long courseId;
    private String courseName;
    private Long coachId;
    private String coachName;
    private LocalDateTime courseTime;
    private String location;
    private BigDecimal price;
    private String status;
    private String paymentStatus;
    private LocalDateTime reservationTime;
    
    /**
     * 审核通过时间（用于计算支付超时）
     */
    private LocalDateTime approvedTime;
    
    /**
     * 课程容量
     */
    private Integer capacity;
    
    /**
     * 当前已预约人数
     */
    private Integer currentReservations;
    
    /**
     * 状态中文映射
     */
    public String getStatusText() {
        switch (status) {
            case "PENDING":
                return "待审核";
            case "APPROVED":
                return "已通过";
            case "REJECTED":
                return "已拒绝";
            case "CANCELLED":
                return "已取消";
            default:
                return status;
        }
    }
    
    /**
     * 支付状态中文映射
     */
    public String getPaymentStatusText() {
        switch (paymentStatus) {
            case "UNPAID":
                return "未支付";
            case "PAID":
                return "已支付";
            default:
                return paymentStatus;
        }
    }
}