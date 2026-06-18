package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("private_coaching")
public class PrivateCoaching {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("member_id")
    private Long memberId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("course_name")
    private String courseName;

    @TableField("course_type")
    private String courseType;

    @TableField("scheduled_time")
    private LocalDateTime scheduledTime;

    @TableField("duration")
    private Integer duration;

    @TableField("price")
    private BigDecimal price;

    @TableField("status")
    private String status;

    @TableField("payment_status")
    private String paymentStatus;

    /**
     * 使用的兑换券ID（使用兑换券时记录）
     */
    @TableField("coupon_id")
    private Long couponId;

    @TableField("location")
    private String location;

    @TableField("notes")
    private String notes;

    @TableField("coach_notes")
    private String coachNotes;

    @TableField("rating")
    private Integer rating;

    @TableField("review")
    private String review;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("complete_time")
    private LocalDateTime completeTime;
}
