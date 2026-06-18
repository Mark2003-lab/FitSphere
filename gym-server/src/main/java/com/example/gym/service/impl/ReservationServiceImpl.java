package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.ReservationVO;
import com.example.gym.entity.*;
import com.example.gym.exception.BusinessException;
import com.example.gym.mapper.*;
import com.example.gym.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    private final ReservationMapper reservationMapper;
    private final CourseMapper courseMapper;
    private final MemberMapper memberMapper;
    private final CoachMapper coachMapper;
    
    public ReservationServiceImpl(ReservationMapper reservationMapper, CourseMapper courseMapper,
                                   MemberMapper memberMapper, CoachMapper coachMapper) {
        this.reservationMapper = reservationMapper;
        this.courseMapper = courseMapper;
        this.memberMapper = memberMapper;
        this.coachMapper = coachMapper;
    }
    
    @Override
    @Transactional
    public Reservation createReservation(Long memberId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (!"AVAILABLE".equals(course.getStatus())) {
            throw new BusinessException("该课程当前不可预约");
        }
        
        if (course.getCourseTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("课程已过期，无法预约");
        }
        
        long currentReservations = reservationMapper.selectCount(
            Wrappers.<Reservation>lambdaQuery()
                .eq(Reservation::getCourseId, courseId)
                .eq(Reservation::getStatus, "APPROVED")
        );
        
        if (currentReservations >= course.getCapacity()) {
            throw new BusinessException("课程已满员");
        }
        
        long userReservation = reservationMapper.selectCount(
            Wrappers.<Reservation>lambdaQuery()
                .eq(Reservation::getMemberId, memberId)
                .eq(Reservation::getCourseId, courseId)
                .in(Reservation::getStatus, "PENDING", "APPROVED")
        );
        
        if (userReservation > 0) {
            throw new BusinessException("您已经预约过该课程");
        }
        
        Reservation reservation = new Reservation();
        reservation.setMemberId(memberId);
        reservation.setCourseId(courseId);
        reservation.setStatus("PENDING");
        reservation.setPaymentStatus("UNPAID");
        reservation.setReservationTime(LocalDateTime.now());
        
        reservationMapper.insert(reservation);
        return reservation;
    }
    
    @Override
    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 只有已通过且未支付或已支付的预约才能取消
        if (!"APPROVED".equals(reservation.getStatus())) {
            throw new BusinessException("只能取消已通过的预约");
        }
        
        // 如果是未支付状态，取消时恢复课程容量（审核通过时已占用容量）
        if ("UNPAID".equals(reservation.getPaymentStatus())) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            if (course != null) {
                Integer currentReservations = course.getCurrentReservations();
                if (currentReservations != null && currentReservations > 0) {
                    course.setCurrentReservations(currentReservations - 1);
                    courseMapper.updateById(course);
                }
            }
        }
        
        reservation.setStatus("CANCELLED");
        reservationMapper.updateById(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        reservationMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void approveReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new BusinessException("只能审核待审核的预约");
        }
        
        Course course = courseMapper.selectById(reservation.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 获取当前课程的已预约人数
        Integer currentReservations = course.getCurrentReservations();
        if (currentReservations == null) {
            currentReservations = 0;
        }
        
        if (currentReservations >= course.getCapacity()) {
            throw new BusinessException("课程已满员");
        }
        
        // 更新预约状态
        reservation.setStatus("APPROVED");
        reservation.setApprovedTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);
        
        // 增加课程已预约人数
        course.setCurrentReservations(currentReservations + 1);
        courseMapper.updateById(course);
    }
    
    @Override
    @Transactional
    public void rejectReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation != null) {
            if (!"PENDING".equals(reservation.getStatus())) {
                throw new BusinessException("只能审核待审核的预约");
            }
            reservation.setStatus("REJECTED");
            reservationMapper.updateById(reservation);
        }
    }
    
    @Override
    @Transactional
    public void payReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (!"APPROVED".equals(reservation.getStatus())) {
            throw new BusinessException("只能支付已通过的预约");
        }
        
        if ("PAID".equals(reservation.getPaymentStatus())) {
            throw new BusinessException("该预约已支付");
        }
        
        // 检查支付是否超时
        if (reservation.getApprovedTime() != null) {
            LocalDateTime timeoutTime = reservation.getApprovedTime().plusMinutes(5);
            if (LocalDateTime.now().isAfter(timeoutTime)) {
                // 超时则取消预约，恢复课程容量
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    Integer currentReservations = course.getCurrentReservations();
                    if (currentReservations != null && currentReservations > 0) {
                        course.setCurrentReservations(currentReservations - 1);
                        courseMapper.updateById(course);
                    }
                }
                
                reservation.setStatus("EXPIRED");
                reservationMapper.updateById(reservation);
                throw new BusinessException("支付超时，预约已失效，请重新预约");
            }
        }
        
        reservation.setPaymentStatus("PAID");
        reservationMapper.updateById(reservation);
    }
    
    @Override
    public Reservation getReservationById(Long id) {
        return reservationMapper.selectById(id);
    }
    
    @Override
    public IPage<Reservation> listReservations(int page, int size) {
        return reservationMapper.selectPage(new Page<>(page, size), Wrappers.emptyWrapper());
    }
    
    @Override
    public IPage<ReservationVO> listReservationsWithDetails(int page, int size) {
        IPage<Reservation> reservationPage = reservationMapper.selectPage(new Page<>(page, size), Wrappers.emptyWrapper());
        
        List<Long> memberIds = reservationPage.getRecords().stream()
                .map(Reservation::getMemberId)
                .collect(Collectors.toList());
        
        List<Long> courseIds = reservationPage.getRecords().stream()
                .map(Reservation::getCourseId)
                .collect(Collectors.toList());
        
        Map<Long, Member> memberMap = memberIds.isEmpty() ? Map.of() :
            memberMapper.selectList(Wrappers.<Member>lambdaQuery().in(Member::getId, memberIds))
                .stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
        
        Map<Long, Course> courseMap = courseIds.isEmpty() ? Map.of() :
            courseMapper.selectList(Wrappers.<Course>lambdaQuery().in(Course::getId, courseIds))
                .stream()
                .collect(Collectors.toMap(Course::getId, course -> course));
        
        List<Long> coachIds = courseMap.values().stream()
                .map(Course::getCoachId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        Map<Long, Coach> coachMap = coachIds.isEmpty() ? Map.of() :
            coachMapper.selectList(Wrappers.<Coach>lambdaQuery().in(Coach::getId, coachIds))
                .stream()
                .collect(Collectors.toMap(Coach::getId, coach -> coach));
        
        List<ReservationVO> voList = reservationPage.getRecords().stream()
                .map(reservation -> {
                    ReservationVO vo = new ReservationVO();
                    vo.setId(reservation.getId());
                    vo.setMemberId(reservation.getMemberId());
                    vo.setCourseId(reservation.getCourseId());
                    vo.setStatus(reservation.getStatus());
                    vo.setPaymentStatus(reservation.getPaymentStatus());
                    vo.setReservationTime(reservation.getReservationTime());
                    vo.setApprovedTime(reservation.getApprovedTime());
                    
                    Member member = memberMap.get(reservation.getMemberId());
                    if (member != null) {
                        vo.setMemberName(member.getName());
                        vo.setMemberPhone(member.getPhone());
                    }
                    
                    Course course = courseMap.get(reservation.getCourseId());
                    if (course != null) {
                        vo.setCourseName(course.getCourseName());
                        vo.setCoachId(course.getCoachId());
                        vo.setCourseTime(course.getCourseTime());
                        vo.setLocation(course.getLocation());
                        vo.setPrice(course.getPrice());
                        vo.setCapacity(course.getCapacity());
                        vo.setCurrentReservations(course.getCurrentReservations());
                        
                        Coach coach = coachMap.get(course.getCoachId());
                        if (coach != null) {
                            vo.setCoachName(coach.getName());
                        }
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
        
        IPage<ReservationVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(reservationPage.getTotal());
        voPage.setCurrent(reservationPage.getCurrent());
        voPage.setSize(reservationPage.getSize());
        
        return voPage;
    }
    
    @Override
    public IPage<ReservationVO> listReservationsByMember(Long memberId, int page, int size) {
        IPage<Reservation> reservationPage = reservationMapper.selectPage(
            new Page<>(page, size),
            Wrappers.<Reservation>lambdaQuery().eq(Reservation::getMemberId, memberId)
        );
        
        return convertToReservationVO(reservationPage);
    }
    
    @Override
    public IPage<ReservationVO> listReservationsByCoach(Long coachId, int page, int size) {
        IPage<Course> coursePage = courseMapper.selectPage(
            new Page<>(1, 1000),
            Wrappers.<Course>lambdaQuery().eq(Course::getCoachId, coachId)
        );
        
        List<Long> courseIds = coursePage.getRecords().stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        if (courseIds.isEmpty()) {
            IPage<ReservationVO> emptyPage = new Page<>();
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(0);
            return emptyPage;
        }
        
        IPage<Reservation> reservationPage = reservationMapper.selectPage(
            new Page<>(page, size),
            Wrappers.<Reservation>lambdaQuery().in(Reservation::getCourseId, courseIds)
        );
        
        return convertToReservationVO(reservationPage);
    }
    
    @Override
    public Reservation getReservationByMemberAndCourse(Long memberId, Long courseId) {
        return reservationMapper.selectOne(
            Wrappers.<Reservation>lambdaQuery()
                .eq(Reservation::getMemberId, memberId)
                .eq(Reservation::getCourseId, courseId)
                .in(Reservation::getStatus, "PENDING", "APPROVED")
        );
    }
    
    @Override
    public long getRemainingPaymentSeconds(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null || reservation.getApprovedTime() == null) {
            return 0;
        }

        LocalDateTime timeoutTime = reservation.getApprovedTime().plusMinutes(5);
        long remainingSeconds = java.time.Duration.between(LocalDateTime.now(), timeoutTime).getSeconds();
        return Math.max(0, remainingSeconds);
    }
    
    private IPage<ReservationVO> convertToReservationVO(IPage<Reservation> reservationPage) {
        List<Long> memberIds = reservationPage.getRecords().stream()
                .map(Reservation::getMemberId)
                .collect(Collectors.toList());
        
        List<Long> courseIds = reservationPage.getRecords().stream()
                .map(Reservation::getCourseId)
                .collect(Collectors.toList());
        
        Map<Long, Member> memberMap = memberIds.isEmpty() ? Map.of() :
            memberMapper.selectList(Wrappers.<Member>lambdaQuery().in(Member::getId, memberIds))
                .stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
        
        Map<Long, Course> courseMap = courseIds.isEmpty() ? Map.of() :
            courseMapper.selectList(Wrappers.<Course>lambdaQuery().in(Course::getId, courseIds))
                .stream()
                .collect(Collectors.toMap(Course::getId, course -> course));
        
        List<Long> coachIds = courseMap.values().stream()
                .map(Course::getCoachId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        Map<Long, Coach> coachMap = coachIds.isEmpty() ? Map.of() :
            coachMapper.selectList(Wrappers.<Coach>lambdaQuery().in(Coach::getId, coachIds))
                .stream()
                .collect(Collectors.toMap(Coach::getId, coach -> coach));
        
        List<ReservationVO> voList = reservationPage.getRecords().stream()
                .map(reservation -> {
                    ReservationVO vo = new ReservationVO();
                    vo.setId(reservation.getId());
                    vo.setMemberId(reservation.getMemberId());
                    vo.setCourseId(reservation.getCourseId());
                    vo.setStatus(reservation.getStatus());
                    vo.setPaymentStatus(reservation.getPaymentStatus());
                    vo.setReservationTime(reservation.getReservationTime());
                    vo.setApprovedTime(reservation.getApprovedTime());
                    
                    Member member = memberMap.get(reservation.getMemberId());
                    if (member != null) {
                        vo.setMemberName(member.getName());
                        vo.setMemberPhone(member.getPhone());
                    }
                    
                    Course course = courseMap.get(reservation.getCourseId());
                    if (course != null) {
                        vo.setCourseName(course.getCourseName());
                        vo.setCoachId(course.getCoachId());
                        vo.setCourseTime(course.getCourseTime());
                        vo.setLocation(course.getLocation());
                        vo.setPrice(course.getPrice());
                        vo.setCapacity(course.getCapacity());
                        vo.setCurrentReservations(course.getCurrentReservations());
                        
                        Coach coach = coachMap.get(course.getCoachId());
                        if (coach != null) {
                            vo.setCoachName(coach.getName());
                        }
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
        
        IPage<ReservationVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(reservationPage.getTotal());
        voPage.setCurrent(reservationPage.getCurrent());
        voPage.setSize(reservationPage.getSize());
        
        return voPage;
    }
}
