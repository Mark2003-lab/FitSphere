package com.example.gym.scheduler;

import com.example.gym.entity.Course;
import com.example.gym.entity.Reservation;
import com.example.gym.mapper.CourseMapper;
import com.example.gym.mapper.ReservationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付超时定时任务
 * 定期检查已通过但未支付的预约，超过5分钟未支付则自动取消并恢复课程容量
 */
@Slf4j
@Component
public class PaymentTimeoutScheduler {

    private final ReservationMapper reservationMapper;
    private final CourseMapper courseMapper;

    public PaymentTimeoutScheduler(ReservationMapper reservationMapper, CourseMapper courseMapper) {
        this.reservationMapper = reservationMapper;
        this.courseMapper = courseMapper;
    }

    /**
     * 每分钟检查一次支付超时的预约
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkPaymentTimeout() {
        log.debug("开始检查支付超时的预约...");
        
        // 查询所有已通过但未支付且超过5分钟的预约
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(5);
        List<Reservation> timeoutReservations = reservationMapper.selectList(
            new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getStatus, "APPROVED")
                .eq(Reservation::getPaymentStatus, "UNPAID")
                .lt(Reservation::getApprovedTime, timeoutTime)
        );
        
        if (timeoutReservations.isEmpty()) {
            log.debug("没有支付超时的预约");
            return;
        }
        
        log.info("发现 {} 个支付超时的预约，正在处理...", timeoutReservations.size());
        
        for (Reservation reservation : timeoutReservations) {
            try {
                // 获取课程信息
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    // 恢复课程容量
                    Integer currentReservations = course.getCurrentReservations();
                    if (currentReservations != null && currentReservations > 0) {
                        course.setCurrentReservations(currentReservations - 1);
                        courseMapper.updateById(course);
                        log.info("预约 {} 支付超时，恢复课程 {} 的容量: {} -> {}", 
                            reservation.getId(), course.getCourseName(), 
                            currentReservations, currentReservations - 1);
                    }
                }
                
                // 更新预约状态为已过期
                reservation.setStatus("EXPIRED");
                reservationMapper.updateById(reservation);
                
            } catch (Exception e) {
                log.error("处理支付超时预约 {} 时发生错误", reservation.getId(), e);
            }
        }
        
        log.info("支付超时检查完成");
    }
}
