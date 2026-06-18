package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("reservation")
public class Reservation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long memberId;
    
    private Long courseId;
    
    private String status;
    
    private String paymentStatus;
    
    private java.time.LocalDateTime reservationTime;
    
    /**
     * 审核通过时间（用于计算支付超时）
     */
    private java.time.LocalDateTime approvedTime;
}
