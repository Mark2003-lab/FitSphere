package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.dto.CheckinDTO;
import com.example.gym.dto.CheckinRankingVO;
import com.example.gym.dto.CheckinStatsVO;
import com.example.gym.dto.CheckinVO;
import com.example.gym.entity.Checkin;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 签到服务接口
 */
public interface CheckinService {
    
    /**
     * 会员自助签到
     * @param memberId 会员ID(从登录用户获取)
     * @param dto 签到请求
     * @return 签到记录
     */
    Checkin checkin(Long memberId, CheckinDTO dto);
    
    /**
     * 会员自助签退
     * @param memberId 会员ID(从登录用户获取)
     * @return 签到记录
     */
    Checkin checkout(Long memberId);
    
    /**
     * 查询会员的签到记录
     * @param memberId 会员ID
     * @param status 状态筛选
     * @return 签到记录列表
     */
    List<CheckinVO> getMemberCheckins(Long memberId, String status);
    
    /**
     * 管理员分页查询所有签到记录
     */
    IPage<CheckinVO> getAllCheckins(int page, int size, Long memberId, Long coachId, 
                                     Long courseId, String status, String type,
                                     LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 管理员补签
     */
    Checkin adminCheckin(CheckinDTO dto);
    
    /**
     * 管理员修改签到记录
     */
    Checkin updateCheckin(Long id, CheckinDTO dto);
    
    /**
     * 管理员删除签到记录
     */
    void deleteCheckin(Long id);
    
    /**
     * 教练查看课程签到情况
     * @param coachId 教练ID
     * @param courseId 课程ID
     * @return 签到记录列表
     */
    List<CheckinVO> getCourseCheckins(Long coachId, Long courseId);
    
    /**
     * 教练查看签到情况（courseId可选）
     * @param coachId 教练ID
     * @param courseId 课程ID（可选，为null时返回该教练所有课程的签到）
     * @return 签到记录列表
     */
    List<CheckinVO> getCoachCheckins(Long coachId, Long courseId);
    
    /**
     * 教练给课程会员签到
     * @param coachId 教练ID
     * @param dto 签到请求
     * @return 签到记录
     */
    Checkin coachCheckinForMember(Long coachId, CheckinDTO dto);
    
    /**
     * 教练取消会员的课程签到
     * @param coachId 教练ID
     * @param courseId 课程ID
     * @param memberId 会员ID
     */
    void cancelCourseCheckin(Long coachId, Long courseId, Long memberId);
    
    /**
     * 获取签到统计数据
     */
    CheckinStatsVO getStats();
    
    /**
     * 获取签到排行榜
     */
    List<CheckinRankingVO> getCheckinRanking();
}
