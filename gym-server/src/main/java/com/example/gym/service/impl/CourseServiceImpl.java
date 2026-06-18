package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.CourseDTO;
import com.example.gym.dto.CourseVO;
import com.example.gym.entity.Coach;
import com.example.gym.entity.Course;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.gym.entity.Reservation;
import com.example.gym.mapper.CoachMapper;
import com.example.gym.mapper.CourseMapper;
import com.example.gym.mapper.ReservationMapper;
import com.example.gym.service.CourseService;
import com.example.gym.service.RedisService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CoachMapper coachMapper;
    private final ReservationMapper reservationMapper;
    private final RedisService redisService;

    private static final String COURSE_LIST_CACHE_KEY = "course:list";
    private static final String COURSE_DETAIL_CACHE_KEY = "course:detail";

    public CourseServiceImpl(CourseMapper courseMapper, CoachMapper coachMapper, ReservationMapper reservationMapper, RedisService redisService) {
        this.courseMapper = courseMapper;
        this.coachMapper = coachMapper;
        this.reservationMapper = reservationMapper;
        this.redisService = redisService;
    }
    
    @Override
    @CacheEvict(value = COURSE_LIST_CACHE_KEY, allEntries = true)
    public Course addCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCoachId(courseDTO.getCoachId());
        course.setCourseTime(courseDTO.getCourseTime());
        course.setCapacity(courseDTO.getCapacity());
        course.setLocation(courseDTO.getLocation());
        course.setPrice(courseDTO.getPrice());
        course.setStatus(courseDTO.getStatus() != null ? courseDTO.getStatus() : "AVAILABLE");
        course.setAuditStatus("APPROVED"); // 管理员直接创建的课程默认已审核
        course.setAuditTime(LocalDateTime.now());

        courseMapper.insert(course);
        return course;
    }

    @Override
    @CacheEvict(value = COURSE_LIST_CACHE_KEY, allEntries = true)
    public Course coachCreateCourse(CourseDTO courseDTO, Long coachId) {
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCoachId(coachId); // 使用传入的教练ID
        course.setCourseTime(courseDTO.getCourseTime());
        course.setCapacity(courseDTO.getCapacity());
        course.setLocation(courseDTO.getLocation());
        course.setPrice(courseDTO.getPrice());
        course.setStatus("PENDING"); // 待审核状态下课程不可预约
        course.setAuditStatus("PENDING"); // 设置为待审核

        courseMapper.insert(course);
        return course;
    }

    @Override
    @CacheEvict(value = {COURSE_LIST_CACHE_KEY, COURSE_DETAIL_CACHE_KEY}, allEntries = true)
    public Course auditCourse(Long courseId, String auditStatus, Long auditorId) {
        Course course = courseMapper.selectById(courseId);
        if (course != null) {
            course.setAuditStatus(auditStatus);
            course.setAuditTime(LocalDateTime.now());
            course.setAuditUserId(auditorId);

            if ("APPROVED".equals(auditStatus)) {
                // 审核通过后，课程状态改为可预约
                course.setStatus("AVAILABLE");
            } else if ("REJECTED".equals(auditStatus)) {
                // 审核拒绝后，课程状态改为已拒绝
                course.setStatus("REJECTED");
            }

            courseMapper.updateById(course);
        }
        return course;
    }

    @Override
    @Cacheable(value = COURSE_LIST_CACHE_KEY, key = "'pending:' + #page + ':' + #size")
    public IPage<Course> listPendingCourses(int page, int size) {
        return courseMapper.selectPage(new Page<>(page, size),
            Wrappers.<Course>lambdaQuery().eq(Course::getAuditStatus, "PENDING"));
    }

    @Override
    @CacheEvict(value = {COURSE_LIST_CACHE_KEY, COURSE_DETAIL_CACHE_KEY}, allEntries = true)
    public Course updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseMapper.selectById(id);
        if (course != null) {
            course.setCourseName(courseDTO.getCourseName());
            course.setCoachId(courseDTO.getCoachId());
            course.setCourseTime(courseDTO.getCourseTime());
            course.setCapacity(courseDTO.getCapacity());
            course.setLocation(courseDTO.getLocation());
            course.setPrice(courseDTO.getPrice());
            course.setStatus(courseDTO.getStatus());
            courseMapper.updateById(course);
        }
        return course;
    }

    @Override
    @CacheEvict(value = {COURSE_LIST_CACHE_KEY, COURSE_DETAIL_CACHE_KEY}, allEntries = true)
    public void deleteCourse(Long id) {
        courseMapper.deleteById(id);
    }

    @Override
    @Cacheable(value = COURSE_DETAIL_CACHE_KEY, key = "'id:' + #id")
    public Course getCourseById(Long id) {
        return courseMapper.selectById(id);
    }

    @Override
    @Cacheable(value = COURSE_LIST_CACHE_KEY, key = "'all:' + #page + ':' + #size")
    public IPage<Course> listCourses(int page, int size) {
        return courseMapper.selectPage(new Page<>(page, size), Wrappers.emptyWrapper());
    }
    
    /**
     * 获取课程列表（包含教练名称和预约人数）
     */
    public IPage<CourseVO> listCoursesWithCoach(int page, int size) {
        IPage<Course> coursePage = courseMapper.selectPage(new Page<>(page, size), 
            Wrappers.<Course>lambdaQuery().eq(Course::getAuditStatus, "APPROVED"));
        
        // 获取所有教练ID
        List<Long> coachIds = coursePage.getRecords().stream()
                .map(Course::getCoachId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        // 获取所有课程ID
        List<Long> courseIds = coursePage.getRecords().stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 查询教练信息
        Map<Long, Coach> coachMap = coachIds.isEmpty() ? Map.of() :
            coachMapper.selectList(Wrappers.<Coach>lambdaQuery().in(Coach::getId, coachIds))
                .stream()
                .collect(Collectors.toMap(Coach::getId, coach -> coach));
        
        // 查询每个课程的已预约人数（只统计已通过的预约）
        Map<Long, Integer> reservationCountMap = courseIds.isEmpty() ? Map.of() :
            reservationMapper.selectList(Wrappers.<Reservation>lambdaQuery()
                    .in(Reservation::getCourseId, courseIds)
                    .eq(Reservation::getStatus, "APPROVED"))
                .stream()
                .collect(Collectors.groupingBy(Reservation::getCourseId, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
        
        // 转换为 CourseVO
        List<CourseVO> voList = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO vo = CourseVO.fromEntity(course);
                    Coach coach = coachMap.get(course.getCoachId());
                    if (coach != null) {
                        vo.setCoachName(coach.getName());
                    }
                    // 设置当前预约人数和剩余名额
                    Integer currentReservations = reservationCountMap.getOrDefault(course.getId(), 0);
                    vo.setCurrentReservations(currentReservations);
                    vo.setAvailableSlots(course.getCapacity() - currentReservations);
                    return vo;
                })
                .collect(Collectors.toList());
        
        IPage<CourseVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(coursePage.getTotal());
        voPage.setCurrent(coursePage.getCurrent());
        voPage.setSize(coursePage.getSize());
        
        return voPage;
    }
    
    public IPage<CourseVO> listCoursesByCoach(Long coachId, int page, int size) {
        IPage<Course> coursePage = courseMapper.selectPage(new Page<>(page, size), 
            Wrappers.<Course>lambdaQuery().eq(Course::getCoachId, coachId));
        
        // 获取所有课程ID
        List<Long> courseIds = coursePage.getRecords().stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 查询每个课程的已预约人数（只统计已通过的预约）
        Map<Long, Integer> reservationCountMap = courseIds.isEmpty() ? Map.of() :
            reservationMapper.selectList(Wrappers.<Reservation>lambdaQuery()
                    .in(Reservation::getCourseId, courseIds)
                    .eq(Reservation::getStatus, "APPROVED"))
                .stream()
                .collect(Collectors.groupingBy(Reservation::getCourseId, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
        
        // 转换为 CourseVO
        List<CourseVO> voList = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO vo = CourseVO.fromEntity(course);
                    Coach coach = coachMapper.selectById(course.getCoachId());
                    if (coach != null) {
                        vo.setCoachName(coach.getName());
                    }
                    // 设置当前预约人数和剩余名额
                    Integer currentReservations = reservationCountMap.getOrDefault(course.getId(), 0);
                    vo.setCurrentReservations(currentReservations);
                    vo.setAvailableSlots(course.getCapacity() - currentReservations);
                    return vo;
                })
                .collect(Collectors.toList());
        
        IPage<CourseVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(coursePage.getTotal());
        voPage.setCurrent(coursePage.getCurrent());
        voPage.setSize(coursePage.getSize());
        
        return voPage;
    }
}