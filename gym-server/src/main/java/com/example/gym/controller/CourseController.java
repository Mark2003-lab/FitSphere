package com.example.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.dto.CourseCheckinDTO;
import com.example.gym.dto.CourseDTO;
import com.example.gym.dto.CourseVO;
import com.example.gym.dto.ReservationVO;
import com.example.gym.entity.Course;
import com.example.gym.entity.User;
import com.example.gym.entity.Member;
import com.example.gym.mapper.CourseMapper;
import com.example.gym.service.CourseService;
import com.example.gym.service.MemberService;
import com.example.gym.service.ReservationService;
import com.example.gym.service.UserService;
import com.example.gym.service.impl.CourseServiceImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    
    private final CourseService courseService;
    private final CourseServiceImpl courseServiceImpl;
    private final UserService userService;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final CourseMapper courseMapper;
    
    public CourseController(CourseService courseService, CourseServiceImpl courseServiceImpl, UserService userService,
                          MemberService memberService, ReservationService reservationService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseServiceImpl = courseServiceImpl;
        this.userService = userService;
        this.memberService = memberService;
        this.reservationService = reservationService;
        this.courseMapper = courseMapper;
    }
    
    /**
     * 管理员创建课程（直接通过审核）
     */
    @PostMapping
    public Response<Course> addCourse(@RequestBody CourseDTO courseDTO) {
        Course course = courseService.addCourse(courseDTO);
        return Response.success("添加成功", course);
    }
    
    /**
     * 教练创建课程（需要审核）
     * POST /api/course/coach/create
     */
    @PostMapping("/coach/create")
    public Response<Course> coachCreateCourse(@RequestBody CourseDTO courseDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.error("用户不存在");
        }
        
        Course course = courseService.coachCreateCourse(courseDTO, user.getId());
        return Response.success("课程创建成功，等待管理员审核", course);
    }
    
    /**
     * 管理员审核课程
     * PUT /api/course/{id}/audit
     */
    @PutMapping("/{id}/audit")
    public Response<Course> auditCourse(@PathVariable Long id, @RequestBody Map<String, String> auditData) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.error("用户不存在");
        }
        
        String auditStatus = auditData.get("auditStatus");
        if (auditStatus == null || (!"APPROVED".equals(auditStatus) && !"REJECTED".equals(auditStatus))) {
            return Response.error("审核状态无效");
        }
        
        Course course = courseService.auditCourse(id, auditStatus, user.getId());
        if (course == null) {
            return Response.error("课程不存在");
        }
        
        String message = "APPROVED".equals(auditStatus) ? "审核通过" : "审核拒绝";
        return Response.success(message, course);
    }
    
    /**
     * 获取待审核课程列表（管理员）
     * GET /api/course/pending
     */
    @GetMapping("/pending")
    public Response<IPage<Course>> listPendingCourses(@RequestParam(defaultValue = "1") int page, 
                                                      @RequestParam(defaultValue = "10") int size) {
        IPage<Course> courses = courseService.listPendingCourses(page, size);
        return Response.success(courses);
    }
    
    @PutMapping("/{id}")
    public Response<Course> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        Course course = courseService.updateCourse(id, courseDTO);
        return Response.success("更新成功", course);
    }
    
    @DeleteMapping("/{id}")
    public Response<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Response.success("删除成功", null);
    }
    
    @GetMapping("/{id}")
    public Response<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return Response.success(course);
    }
    
    /**
     * 获取所有课程列表（仅已审核通过的）
     */
    @GetMapping
    public Response<IPage<CourseVO>> listCourses(@RequestParam(defaultValue = "1") int page, 
                                                  @RequestParam(defaultValue = "10") int size) {
        IPage<CourseVO> courses = courseServiceImpl.listCoursesWithCoach(page, size);
        return Response.success(courses);
    }
    
    /**
     * 获取教练自己的课程（包括待审核、已通过、已拒绝）
     */
    @GetMapping("/coach")
    public Response<IPage<CourseVO>> listCoachCourses(@RequestParam(defaultValue = "1") int page, 
                                                      @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.error("用户不存在");
        }
        IPage<CourseVO> courses = courseServiceImpl.listCoursesByCoach(user.getId(), page, size);
        return Response.success(courses);
    }
    
    /**
     * 发布课程签到
     * POST /api/course/{id}/checkin/publish
     */
    @PostMapping("/{id}/checkin/publish")
    public Response<Map<String, Object>> publishCheckin(@PathVariable Long id, @RequestBody(required = false) CourseCheckinDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.error("用户不存在");
        }
        
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return Response.error("课程不存在");
        }
        
        // 检查课程是否已通过审核
        if (!"APPROVED".equals(course.getAuditStatus())) {
            return Response.error("课程未通过审核，无法发布签到");
        }
        
        if (!course.getCoachId().equals(user.getId())) {
            return Response.error("无权操作其他教练的课程");
        }
        
        // 设置签到时间，默认持续30分钟
        int duration = dto != null && dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 30;
        LocalDateTime now = LocalDateTime.now();
        course.setCheckinStartTime(now);
        course.setCheckinEndTime(now.plusMinutes(duration));
        course.setCheckinEnabled(true);
        courseMapper.updateById(course);
        
        Map<String, Object> result = new HashMap<>();
        result.put("courseId", id);
        result.put("checkinStartTime", course.getCheckinStartTime());
        result.put("checkinEndTime", course.getCheckinEndTime());
        result.put("durationMinutes", duration);
        
        return Response.success("签到已发布", result);
    }
    
    /**
     * 停止课程签到
     * POST /api/course/{id}/checkin/stop
     */
    @PostMapping("/{id}/checkin/stop")
    public Response<Void> stopCheckin(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.error("用户不存在");
        }
        
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return Response.error("课程不存在");
        }
        
        if (!course.getCoachId().equals(user.getId())) {
            return Response.error("无权操作其他教练的课程");
        }
        
        course.setCheckinEnabled(false);
        courseMapper.updateById(course);
        
        return Response.success("签到已停止", null);
    }
    
    /**
     * 获取课程签到状态
     * GET /api/course/{id}/checkin/status
     */
    @GetMapping("/{id}/checkin/status")
    public Response<Map<String, Object>> getCheckinStatus(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return Response.error("课程不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("courseId", id);
        result.put("checkinEnabled", course.getCheckinEnabled());
        result.put("checkinStartTime", course.getCheckinStartTime());
        result.put("checkinEndTime", course.getCheckinEndTime());
        
        // 判断是否在签到时间内
        LocalDateTime now = LocalDateTime.now();
        boolean isInCheckinTime = course.getCheckinEnabled() != null && course.getCheckinEnabled()
                && course.getCheckinStartTime() != null && course.getCheckinEndTime() != null
                && now.isAfter(course.getCheckinStartTime()) && now.isBefore(course.getCheckinEndTime());
        result.put("isInCheckinTime", isInCheckinTime);
        
        return Response.success(result);
    }
    
    /**
     * 获取会员预约的课程列表（包含签到状态）
     * GET /api/course/member
     */
    @GetMapping("/member")
    public Response<List<Course>> getMemberCourses() {
        Long memberId = getCurrentMemberId();
        
        // 查询会员的预约记录
        IPage<ReservationVO> reservationPage = 
            reservationService.listReservationsByMember(memberId, 1, 1000);
        
        // 获取课程ID列表
        List<Long> courseIds = reservationPage.getRecords().stream()
            .map(ReservationVO::getCourseId)
            .collect(Collectors.toList());
        
        if (courseIds.isEmpty()) {
            return Response.success(List.of());
        }
        
        // 查询课程信息（只查询已审核通过的课程）
        LambdaQueryWrapper<Course> courseQuery = new LambdaQueryWrapper<>();
        courseQuery.in(Course::getId, courseIds)
                   .eq(Course::getAuditStatus, "APPROVED");
        List<Course> courses = courseMapper.selectList(courseQuery);
        
        return Response.success(courses);
    }
    
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
}