package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("course_name")
    private String courseName;
    
    @TableField("coach_id")
    private Long coachId;
    
    @TableField("course_time")
    private LocalDateTime courseTime;
    
    @TableField("capacity")
    private Integer capacity;
    
    /** 当前已预约人数 */
    @TableField("current_reservations")
    private Integer currentReservations;
    
    @TableField("location")
    private String location;
    
    private java.math.BigDecimal price;
    
    private String status;
    
    /** 审核状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝 */
    @TableField("audit_status")
    private String auditStatus;
    
    /** 审核时间 */
    @TableField("audit_time")
    private LocalDateTime auditTime;
    
    /** 审核人ID */
    @TableField("audit_user_id")
    private Long auditUserId;
    
    /** 签到开始时间 */
    @TableField("checkin_start_time")
    private LocalDateTime checkinStartTime;
    
    /** 签到结束时间 */
    @TableField("checkin_end_time")
    private LocalDateTime checkinEndTime;
    
    /** 是否开启签到 */
    @TableField("checkin_enabled")
    private Boolean checkinEnabled;
}