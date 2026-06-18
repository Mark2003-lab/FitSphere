package com.example.gym.scheduler;

import com.example.gym.entity.Reservation;
import com.example.gym.mapper.ReservationMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 预约支付超时处理定时任务
 */
@Slf4j
@Component
public class ReservationTimeoutScheduler {

    // 支付超时时间（分钟）
    private static final int PAYMENT_TIMEOUT_MINUTES = 5;

    private final ReservationMapper reservationMapper;

    public ReservationTimeoutScheduler(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    /**
     * 每分钟检查一次超时预约
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processTimeoutReservations() {
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES);

        // 查询所有已审核通过但未支付且已超时的预约
        var timeoutReservations = reservationMapper.selectList(
            Wrappers.<Reservation>lambdaQuery()
                .eq(Reservation::getStatus, "APPROVED")
                .eq(Reservation::getPaymentStatus, "UNPAID")
                .lt(Reservation::getApprovedTime, timeoutTime)
        );

        if (!timeoutReservations.isEmpty()) {
            log.info("发现 {} 个支付超时的预约，正在取消", timeoutReservations.size());

            for (Reservation reservation : timeoutReservations) {
                // 取消预约，恢复课程容量
                reservation.setStatus("EXPIRED");
                reservationMapper.updateById(reservation);
                log.info("预约 {} 因支付超时已取消", reservation.getId());
            }
        }
    }

    /**
     * 检查单个预约是否已超时
     */
    public boolean isPaymentTimeout(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return false;
        }
        
        if (!"APPROVED".equals(reservation.getStatus()) || !"UNPAID".equals(reservation.getPaymentStatus())) {
            return false;
        }
        
        if (reservation.getApprovedTime() == null) {
            return false;
        }
        
        LocalDateTime timeoutTime = reservation.getApprovedTime().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        return LocalDateTime.now().isAfter(timeoutTime);
    }

    /**
     * 获取剩余支付时间（秒）
     */
    public long getRemainingPaymentSeconds(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null || reservation.getApprovedTime() == null) {
            return 0;
        }

        LocalDateTime timeoutTime = reservation.getApprovedTime().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        long remainingSeconds = java.time.Duration.between(LocalDateTime.now(), timeoutTime).getSeconds();
        return Math.max(0, remainingSeconds);
    }
}
