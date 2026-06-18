package com.example.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 签到排行榜VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckinRankingVO {
    
    /**
     * 排名
     */
    private Integer rank;
    
    /**
     * 会员ID
     */
    private Long memberId;
    
    /**
     * 会员姓名
     */
    private String memberName;
    
    /**
     * 会员手机号
     */
    private String memberPhone;
    
    /**
     * 会员等级
     */
    private String memberLevel;
    
    /**
     * 会员头像
     */
    private String memberAvatar;
    
    /**
     * 累计签到次数
     */
    private Integer checkinCount;
    
    /**
     * 连续签到天数
     */
    private Integer consecutiveDays;
    
    /**
     * 本月签到次数
     */
    private Integer monthCheckinCount;
    
    /**
     * 本周签到次数
     */
    private Integer weekCheckinCount;
}
