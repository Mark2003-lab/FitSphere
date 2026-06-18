package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.CheckinDTO;
import com.example.gym.dto.CheckinRankingVO;
import com.example.gym.dto.CheckinStatsVO;
import com.example.gym.dto.CheckinVO;
import com.example.gym.entity.Checkin;
import com.example.gym.entity.Course;
import com.example.gym.entity.Member;
import com.example.gym.entity.Reservation;
import com.example.gym.exception.BusinessException;
import com.example.gym.mapper.CheckinMapper;
import com.example.gym.mapper.CourseMapper;
import com.example.gym.mapper.MemberMapper;
import com.example.gym.mapper.ReservationMapper;
import com.example.gym.service.CheckinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 签到服务实现类
 */
@Service
public class CheckinServiceImpl implements CheckinService {
    
    private final CheckinMapper checkinMapper;
    private final CourseMapper courseMapper;
    private final ReservationMapper reservationMapper;
    private final MemberMapper memberMapper;
    
    public CheckinServiceImpl(CheckinMapper checkinMapper, CourseMapper courseMapper,
                             ReservationMapper reservationMapper, MemberMapper memberMapper) {
        this.checkinMapper = checkinMapper;
        this.courseMapper = courseMapper;
        this.reservationMapper = reservationMapper;
        this.memberMapper = memberMapper;
    }
    
    @Override
    @Transactional
    public Checkin checkin(Long memberId, CheckinDTO dto) {
        String type = dto.getType() != null ? dto.getType() : "GYM";
        
        // GYM签到：当天已签到则拒绝重复签到
        if ("GYM".equals(type)) {
            LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
            QueryWrapper<Checkin> gymQuery = new QueryWrapper<>();
            gymQuery.eq("member_id", memberId)
                .eq("type", "GYM")
                .ge("checkin_time", todayStart);
            Long gymCount = checkinMapper.selectCount(gymQuery);
            if (gymCount != null && gymCount > 0) {
                throw new BusinessException("今天已经签到过了，无需重复签到");
            }
        }
        
        // 课程签到：同课程已有签到则拒绝重复签到
        if ("COURSE".equals(type) && dto.getCourseId() != null) {
            QueryWrapper<Checkin> courseQuery = new QueryWrapper<>();
            courseQuery.eq("member_id", memberId)
                .eq("course_id", dto.getCourseId())
                .eq("type", "COURSE");
            Long courseCount = checkinMapper.selectCount(courseQuery);
            if (courseCount != null && courseCount > 0) {
                throw new BusinessException("该课程已签到，无需重复签到");
            }
        }
        
        // 创建签到记录
        Checkin checkin = new Checkin();
        checkin.setMemberId(memberId);
        checkin.setCheckinTime(dto.getCheckinTime() != null ? dto.getCheckinTime() : LocalDateTime.now());
        checkin.setStatus("CHECKED_IN");
        checkin.setType(type);
        checkin.setCourseId(dto.getCourseId());
        checkin.setCoachId(dto.getCoachId());
        checkin.setRemark(dto.getRemark());
        
        checkinMapper.insert(checkin);
        return checkin;
    }
    
    @Override
    @Transactional
    public Checkin checkout(Long memberId) {
        // 1. 查找当前未签退的记录
        Checkin activeCheckin = getCurrentActiveCheckin(memberId);
        if (activeCheckin == null) {
            throw new BusinessException("当前没有签到记录,无法签退");
        }
        
        // 2. 更新签退时间和状态
        activeCheckin.setCheckoutTime(LocalDateTime.now());
        activeCheckin.setStatus("CHECKED_OUT");
        checkinMapper.updateById(activeCheckin);
        
        return activeCheckin;
    }
    
    @Override
    public List<CheckinVO> getMemberCheckins(Long memberId, String status) {
        return checkinMapper.selectMemberCheckins(memberId, status);
    }
    
    @Override
    public IPage<CheckinVO> getAllCheckins(int page, int size, Long memberId, Long coachId, 
                                            Long courseId, String status, String type,
                                            LocalDateTime startTime, LocalDateTime endTime) {
        Page<CheckinVO> pageParam = new Page<>(page, size);
        return checkinMapper.selectCheckinPage(pageParam, memberId, coachId, courseId, 
                                                status, type, startTime, endTime);
    }
    
    @Override
    @Transactional
    public Checkin adminCheckin(CheckinDTO dto) {
        // 管理员补签,允许指定时间和会员
        if (dto.getMemberId() == null) {
            throw new BusinessException("会员ID不能为空");
        }
        
        Checkin checkin = new Checkin();
        checkin.setMemberId(dto.getMemberId());
        checkin.setCheckinTime(dto.getCheckinTime() != null ? dto.getCheckinTime() : LocalDateTime.now());
        checkin.setCheckoutTime(dto.getCheckoutTime());
        checkin.setStatus(dto.getCheckoutTime() != null ? "CHECKED_OUT" : "CHECKED_IN");
        checkin.setType(dto.getType() != null ? dto.getType() : "GYM");
        checkin.setCourseId(dto.getCourseId());
        checkin.setCoachId(dto.getCoachId());
        checkin.setRemark(dto.getRemark());
        
        checkinMapper.insert(checkin);
        return checkin;
    }
    
    @Override
    @Transactional
    public Checkin updateCheckin(Long id, CheckinDTO dto) {
        Checkin checkin = checkinMapper.selectById(id);
        if (checkin == null) {
            throw new BusinessException("签到记录不存在");
        }
        
        // 更新字段
        if (dto.getMemberId() != null) {
            checkin.setMemberId(dto.getMemberId());
        }
        if (dto.getCheckinTime() != null) {
            checkin.setCheckinTime(dto.getCheckinTime());
        }
        if (dto.getCheckoutTime() != null) {
            checkin.setCheckoutTime(dto.getCheckoutTime());
            checkin.setStatus("CHECKED_OUT");
        }
        if (dto.getType() != null) {
            checkin.setType(dto.getType());
        }
        if (dto.getCourseId() != null) {
            checkin.setCourseId(dto.getCourseId());
        }
        if (dto.getCoachId() != null) {
            checkin.setCoachId(dto.getCoachId());
        }
        if (dto.getRemark() != null) {
            checkin.setRemark(dto.getRemark());
        }
        
        checkinMapper.updateById(checkin);
        return checkin;
    }
    
    @Override
    @Transactional
    public void deleteCheckin(Long id) {
        Checkin checkin = checkinMapper.selectById(id);
        if (checkin == null) {
            throw new BusinessException("签到记录不存在");
        }
        checkinMapper.deleteById(id);
    }
    
    @Override
    public List<CheckinVO> getCourseCheckins(Long coachId, Long courseId) {
        // 验证教练是否是该课程的教练
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (!course.getCoachId().equals(coachId)) {
            throw new BusinessException("无权查看其他教练的课程签到");
        }
        
        // 查询该课程的签到记录
        Page<CheckinVO> pageParam = new Page<>(1, 1000);
        IPage<CheckinVO> result = checkinMapper.selectCheckinPage(
            pageParam, null, null, courseId, null, "COURSE", null, null);
        return result.getRecords();
    }
    
    @Override
    public List<CheckinVO> getCoachCheckins(Long coachId, Long courseId) {
        // 查询该教练的所有课程
        QueryWrapper<Course> courseQuery = new QueryWrapper<>();
        courseQuery.eq("coach_id", coachId);
        if (courseId != null) {
            courseQuery.eq("id", courseId);
        }
        List<Course> courses = courseMapper.selectList(courseQuery);
        if (courses.isEmpty()) {
            return List.of();
        }
        
        // 获取课程ID列表
        List<Long> courseIds = courses.stream()
            .map(Course::getId)
            .collect(java.util.stream.Collectors.toList());
        
        // 查询这些课程的所有预约记录（待审核和已通过的）
        QueryWrapper<Reservation> reservationQuery = new QueryWrapper<>();
        reservationQuery.in("course_id", courseIds)
            .in("status", "PENDING", "APPROVED")
            .orderByDesc("reservation_time");
        List<Reservation> reservations = reservationMapper.selectList(reservationQuery);
        
        // 将预约记录转换为CheckinVO格式
        List<CheckinVO> result = new java.util.ArrayList<>();
        for (Reservation reservation : reservations) {
            // 获取会员信息
            Member member = memberMapper.selectById(reservation.getMemberId());
            // 获取课程信息
            Course course = courseMapper.selectById(reservation.getCourseId());
            
            CheckinVO vo = new CheckinVO();
            vo.setMemberId(reservation.getMemberId());
            vo.setCourseId(reservation.getCourseId());
            vo.setMemberName(member != null ? member.getName() : "");
            vo.setMemberPhone(member != null ? member.getPhone() : "");
            vo.setCourseName(course != null ? course.getCourseName() : "");
            vo.setCourseTime(course != null ? course.getCourseTime() : null);
            vo.setReservationStatus(reservation.getStatus());
            vo.setPaymentStatus(reservation.getPaymentStatus());
            vo.setCheckinStatus("UNCHECKED"); // 默认未签到
            
            // 检查是否已有签到记录
            QueryWrapper<Checkin> checkinQuery = new QueryWrapper<>();
            checkinQuery.eq("member_id", reservation.getMemberId())
                .eq("course_id", reservation.getCourseId())
                .eq("type", "COURSE")
                .eq("status", "CHECKED_IN");
            Checkin existingCheckin = checkinMapper.selectOne(checkinQuery);
            if (existingCheckin != null) {
                vo.setCheckinStatus("CHECKED_IN");
                vo.setCheckinTime(existingCheckin.getCheckinTime());
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public Checkin coachCheckinForMember(Long coachId, CheckinDTO dto) {
        // 1. 验证教练权限
        if (dto.getCourseId() == null) {
            throw new BusinessException("课程ID不能为空");
        }
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (!course.getCoachId().equals(coachId)) {
            throw new BusinessException("无权为其他教练的课程会员签到");
        }
        
        // 2. 验证会员ID
        if (dto.getMemberId() == null) {
            throw new BusinessException("会员ID不能为空");
        }
        
        // 3. 检查是否已有签到记录
        QueryWrapper<Checkin> existingQuery = new QueryWrapper<>();
        existingQuery.eq("member_id", dto.getMemberId())
            .eq("course_id", dto.getCourseId())
            .eq("type", "COURSE")
            .eq("status", "CHECKED_IN");
        Checkin existing = checkinMapper.selectOne(existingQuery);
        if (existing != null) {
            throw new BusinessException("该会员已签到，无需重复签到");
        }
        
        // 4. 创建签到记录
        Checkin checkin = new Checkin();
        checkin.setMemberId(dto.getMemberId());
        checkin.setCheckinTime(dto.getCheckinTime() != null ? dto.getCheckinTime() : LocalDateTime.now());
        checkin.setStatus("CHECKED_IN");
        checkin.setType("COURSE");
        checkin.setCourseId(dto.getCourseId());
        checkin.setCoachId(coachId);
        checkin.setRemark(dto.getRemark());
        
        checkinMapper.insert(checkin);
        return checkin;
    }
    
    @Override
    public CheckinStatsVO getStats() {
        CheckinStatsVO stats = new CheckinStatsVO();
        stats.setTodayCheckinCount(checkinMapper.countTodayCheckins());
        stats.setCurrentInGymCount(checkinMapper.countCurrentInGym());
        stats.setWeekCheckinCount(checkinMapper.countWeekCheckins());
        
        // 计算课程签到率(所有课程的平均签到率)
        List<Course> courses = courseMapper.selectList(null);
        if (courses.isEmpty()) {
            stats.setCourseCheckinRate(0.0);
        } else {
            double totalRate = 0;
            int courseCount = 0;
            for (Course course : courses) {
                Double rate = checkinMapper.calculateCourseCheckinRate(course.getId());
                if (rate != null) {
                    totalRate += rate;
                    courseCount++;
                }
            }
            stats.setCourseCheckinRate(courseCount > 0 ? totalRate / courseCount : 0.0);
        }
        
        return stats;
    }
    
    @Override
    public List<CheckinRankingVO> getCheckinRanking() {
        List<CheckinRankingVO> ranking = checkinMapper.selectCheckinRanking();
        
        // 设置排名
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setRank(i + 1);
            
            // 填充额外数据
            Long memberId = ranking.get(i).getMemberId();
            ranking.get(i).setConsecutiveDays(checkinMapper.countConsecutiveCheckinDays(memberId));
            ranking.get(i).setMonthCheckinCount(checkinMapper.countMonthCheckins(memberId));
            ranking.get(i).setWeekCheckinCount(checkinMapper.countWeekCheckinsByMember(memberId));
        }
        
        return ranking;
    }
    
    @Override
    @Transactional
    public void cancelCourseCheckin(Long coachId, Long courseId, Long memberId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (!course.getCoachId().equals(coachId)) {
            throw new BusinessException("无权操作其他教练的课程");
        }
        
        QueryWrapper<Checkin> query = new QueryWrapper<>();
        query.eq("member_id", memberId)
            .eq("course_id", courseId)
            .eq("type", "COURSE")
            .eq("status", "CHECKED_IN");
        Checkin checkin = checkinMapper.selectOne(query);
        if (checkin == null) {
            throw new BusinessException("该会员暂无签到记录");
        }
        checkinMapper.deleteById(checkin);
    }
    
    /**
     * 获取会员当前未签退的签到记录
     */
    private Checkin getCurrentActiveCheckin(Long memberId) {
        QueryWrapper<Checkin> query = new QueryWrapper<>();
        query.eq("member_id", memberId)
             .eq("status", "CHECKED_IN")
             .isNull("checkout_time")
             .orderByDesc("checkin_time")
             .last("LIMIT 1");
        return checkinMapper.selectOne(query);
    }
}
