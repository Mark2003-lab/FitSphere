package com.example.gym.dto;

import lombok.Data;

/**
 * 签到统计VO
 */
@Data
public class CheckinStatsVO {
    
    /** 今日签到人数 */
    private Long todayCheckinCount;
    
    /** 当前在馆人数(已签到未签退) */
    private Long currentInGymCount;
    
    /** 本周签到次数 */
    private Long weekCheckinCount;
    
    /** 课程签到率(%) */
    private Double courseCheckinRate;
}
