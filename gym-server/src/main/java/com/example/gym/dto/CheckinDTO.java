package com.example.gym.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到请求DTO
 */
@Data
public class CheckinDTO {
    
    /** 会员ID(管理员代签时使用,会员自助签到时自动从登录用户获取) */
    private Long memberId;

    /** 签到类型: GYM(健身房) / COURSE(课程) */
    private String type;

    /** 课程ID(课程签到时必填) */
    private Long courseId;

    /** 教练ID(课程签到时自动从当前教练获取) */
    private Long coachId;

    /** 备注 */
    private String remark;

    /** 签到时间(管理员补签时使用,默认为当前时间) */
    private LocalDateTime checkinTime;

    /** 签退时间(管理员补签时使用) */
    private LocalDateTime checkoutTime;
}
