package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.dto.CheckinDTO;
import com.example.gym.dto.CheckinRankingVO;
import com.example.gym.dto.CheckinStatsVO;
import com.example.gym.dto.CheckinVO;
import com.example.gym.entity.Checkin;
import com.example.gym.entity.Coach;
import com.example.gym.entity.Course;
import com.example.gym.entity.Member;
import com.example.gym.entity.User;
import com.example.gym.service.CheckinService;
import com.example.gym.service.CoachService;
import com.example.gym.service.CourseService;
import com.example.gym.service.MemberService;
import com.example.gym.service.ReservationService;
import com.example.gym.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 签到管理控制器
 */
@RestController
@RequestMapping("/api/checkin")
public class CheckinController {
    
    private final CheckinService checkinService;
    private final UserService userService;
    private final MemberService memberService;
    private final CoachService coachService;
    private final CourseService courseService;
    private final ReservationService reservationService;
    
    public CheckinController(CheckinService checkinService, UserService userService, MemberService memberService, 
                            CoachService coachService, CourseService courseService, ReservationService reservationService) {
        this.checkinService = checkinService;
        this.userService = userService;
        this.memberService = memberService;
        this.coachService = coachService;
        this.courseService = courseService;
        this.reservationService = reservationService;
    }
    
    // ========== 会员自助签到接口 ==========
    
    /**
     * 会员自助签到
     * POST /api/checkin/me
     */
    @PostMapping("/me")
    public Response<Checkin> memberCheckin(@RequestBody(required = false) CheckinDTO dto) {
        Long memberId = getCurrentMemberId();
        if (dto == null) {
            dto = new CheckinDTO();
        }
        Checkin checkin = checkinService.checkin(memberId, dto);
        return Response.success("签到成功", checkin);
    }
    
    /**
     * 会员课程签到（在签到时间内可以自行签到）
     * POST /api/checkin/course/{courseId}
     */
    @PostMapping("/course/{courseId}")
    public Response<Checkin> memberCourseCheckin(@PathVariable Long courseId) {
        Long memberId = getCurrentMemberId();
        
        // 验证课程是否正在签到时间段内
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return Response.error("课程不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        // 检查是否在签到时间内
        if (course.getCheckinEnabled() == null || !course.getCheckinEnabled()
                || course.getCheckinStartTime() == null || course.getCheckinEndTime() == null
                || now.isBefore(course.getCheckinStartTime()) || now.isAfter(course.getCheckinEndTime())) {
            return Response.error("当前不在课程签到时间内");
        }
        
        // 检查会员是否预约了该课程
        com.example.gym.entity.Reservation reservation = reservationService.getReservationByMemberAndCourse(memberId, courseId);
        if (reservation == null || !"APPROVED".equals(reservation.getStatus())) {
            return Response.error("您未预约该课程或预约未通过");
        }
        
        // 创建课程签到记录
        CheckinDTO dto = new CheckinDTO();
        dto.setCourseId(courseId);
        dto.setCoachId(course.getCoachId());
        dto.setType("COURSE");
        Checkin checkin = checkinService.checkin(memberId, dto);
        
        return Response.success("课程签到成功", checkin);
    }
    
    /**
     * 会员自助签退
     * PUT /api/checkin/me/checkout
     */
    @PutMapping("/me/checkout")
    public Response<Checkin> memberCheckout() {
        Long memberId = getCurrentMemberId();
        Checkin checkin = checkinService.checkout(memberId);
        return Response.success("签退成功", checkin);
    }
    
    /**
     * 查询我的签到记录
     * GET /api/checkin/my
     */
    @GetMapping("/my")
    public Response<List<CheckinVO>> getMyCheckins(@RequestParam(required = false) String status) {
        Long memberId = getCurrentMemberId();
        List<CheckinVO> checkins = checkinService.getMemberCheckins(memberId, status);
        return Response.success(checkins);
    }
    
    // ========== 教练课堂签到接口 ==========
    
    /**
     * 教练查看课程签到情况
     * GET /api/checkin/coach/courses?courseId=xxx (courseId可选)
     */
    @GetMapping("/coach/courses")
    public Response<List<CheckinVO>> getCourseCheckins(@RequestParam(required = false) Long courseId) {
        Long coachId = getCurrentCoachId();
        List<CheckinVO> checkins = checkinService.getCoachCheckins(coachId, courseId);
        return Response.success(checkins);
    }
    
    /**
     * 教练给课程会员签到
     * POST /api/checkin/course
     */
    @PostMapping("/course")
    public Response<Checkin> coachCheckinForMember(@RequestBody CheckinDTO dto) {
        Long coachId = getCurrentCoachId();
        Checkin checkin = checkinService.coachCheckinForMember(coachId, dto);
        return Response.success("签到成功", checkin);
    }
    
    /**
     * 教练取消会员的课程签到
     * DELETE /api/checkin/course/{courseId}/member/{memberId}
     */
    @DeleteMapping("/course/{courseId}/member/{memberId}")
    public Response<Void> cancelCourseCheckin(@PathVariable Long courseId, @PathVariable Long memberId) {
        Long coachId = getCurrentCoachId();
        checkinService.cancelCourseCheckin(coachId, courseId, memberId);
        return Response.success("已取消签到", null);
    }
    
    // ========== 管理员全局管理接口 ==========
    
    /**
     * 管理员查看全部签到记录
     * GET /api/checkin
     */
    @GetMapping
    public Response<IPage<CheckinVO>> getAllCheckins(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long coachId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        IPage<CheckinVO> checkins = checkinService.getAllCheckins(
            page, size, memberId, coachId, courseId, status, type, startTime, endTime);
        return Response.success(checkins);
    }
    
    /**
     * 管理员补签
     * POST /api/checkin
     */
    @PostMapping
    public Response<Checkin> adminCheckin(@RequestBody CheckinDTO dto) {
        Checkin checkin = checkinService.adminCheckin(dto);
        return Response.success("补签成功", checkin);
    }
    
    /**
     * 管理员修改签到记录
     * PUT /api/checkin/{id}
     */
    @PutMapping("/{id}")
    public Response<Checkin> updateCheckin(@PathVariable Long id, @RequestBody CheckinDTO dto) {
        Checkin checkin = checkinService.updateCheckin(id, dto);
        return Response.success("修改成功", checkin);
    }
    
    /**
     * 管理员删除签到记录
     * DELETE /api/checkin/{id}
     */
    @DeleteMapping("/{id}")
    public Response<Void> deleteCheckin(@PathVariable Long id) {
        checkinService.deleteCheckin(id);
        return Response.success("删除成功", null);
    }
    
    /**
     * 获取签到统计数据
     * GET /api/checkin/stats
     */
    @GetMapping("/stats")
    public Response<CheckinStatsVO> getStats() {
        CheckinStatsVO stats = checkinService.getStats();
        return Response.success(stats);
    }
    
    /**
     * 获取签到排行榜
     * GET /api/checkin/ranking
     */
    @GetMapping("/ranking")
    public Response<List<CheckinRankingVO>> getCheckinRanking() {
        List<CheckinRankingVO> ranking = checkinService.getCheckinRanking();
        return Response.success(ranking);
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 获取当前登录会员的ID
     * 查找策略：先按 ID 查，再按手机号查，都不存在则自动创建
     */
    private Long getCurrentMemberId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 1. 先按 user.id 查找 member 表
        Member member = memberService.getMemberById(user.getId());
        if (member != null) {
            return member.getId();
        }
        // 2. 按手机号查找（处理 user/member ID 不一致的历史数据）
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            Member phoneMember = memberService.getMemberByPhone(user.getPhone());
            if (phoneMember != null) {
                return phoneMember.getId();
            }
        }
        // 3. 都不存在，自动创建新 member 记录
        Member newMember = new Member();
        newMember.setId(user.getId());
        newMember.setName(user.getUsername());
        newMember.setPhone(user.getPhone() != null ? user.getPhone() : "");
        newMember.setGender(user.getGender() != null ? user.getGender() : "男");
        newMember.setLevel("普通会员");
        newMember.setJoinDate(java.time.LocalDateTime.now());
        memberService.addMemberEntity(newMember);
        return newMember.getId();
    }
    
    /**
     * 获取当前登录教练的ID
     * 通过 user 表查询关联的 coach 表记录
     */
    private Long getCurrentCoachId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 通过 user.id 查询 coach 表
        Coach coach = coachService.getCoachById(user.getId());
        if (coach == null) {
            throw new RuntimeException("教练记录不存在");
        }
        return coach.getId();
    }
}
