package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到记录实体类
 */
@Data
@TableName("checkin")
public class Checkin {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 会员ID */
    private Long memberId;

    /** 签到时间 */
    private LocalDateTime checkinTime;

    /** 签退时间 */
    private LocalDateTime checkoutTime;

    /** 状态: CHECKED_IN(已签到) / CHECKED_OUT(已签退) */
    private String status;

    /** 类型: GYM(健身房签到) / COURSE(课程签到) */
    private String type;

    /** 课程ID(可为空) */
    private Long courseId;

    /** 教练ID(可为空) */
    private Long coachId;

    /** 备注 */
    private String remark;
}
