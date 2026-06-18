package com.example.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.CheckinRankingVO;
import com.example.gym.dto.CheckinVO;
import com.example.gym.entity.Checkin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 签到记录Mapper接口
 */
@Mapper
public interface CheckinMapper extends BaseMapper<Checkin> {
    
    /**
     * 分页查询签到记录(关联会员、课程、教练信息)
     */
    IPage<CheckinVO> selectCheckinPage(Page<CheckinVO> page, 
                                        @Param("memberId") Long memberId,
                                        @Param("coachId") Long coachId,
                                        @Param("courseId") Long courseId,
                                        @Param("status") String status,
                                        @Param("type") String type,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询会员的签到记录
     */
    List<CheckinVO> selectMemberCheckins(@Param("memberId") Long memberId,
                                          @Param("status") String status);
    
    /**
     * 统计今日签到人数
     */
    Long countTodayCheckins();
    
    /**
     * 统计当前在馆人数
     */
    Long countCurrentInGym();
    
    /**
     * 统计本周签到次数
     */
    Long countWeekCheckins();
    
    /**
     * 统计课程签到率
     */
    Double calculateCourseCheckinRate(@Param("courseId") Long courseId);
    
    /**
     * 查询签到排行榜（按累计签到次数排序）
     */
    List<CheckinRankingVO> selectCheckinRanking();
    
    /**
     * 查询会员的连续签到天数
     */
    Integer countConsecutiveCheckinDays(@Param("memberId") Long memberId);
    
    /**
     * 查询会员本月签到次数
     */
    Integer countMonthCheckins(@Param("memberId") Long memberId);
    
    /**
     * 查询会员本周签到次数
     */
    Integer countWeekCheckinsByMember(@Param("memberId") Long memberId);
}
