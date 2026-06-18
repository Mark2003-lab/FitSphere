package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.dto.ReservationVO;
import com.example.gym.entity.Reservation;

public interface ReservationService {
    Reservation createReservation(Long memberId, Long courseId);
    void cancelReservation(Long id);
    void deleteReservation(Long id);
    void approveReservation(Long id);
    void rejectReservation(Long id);
    void payReservation(Long id);
    Reservation getReservationById(Long id);
    IPage<Reservation> listReservations(int page, int size);
    IPage<ReservationVO> listReservationsWithDetails(int page, int size);
    IPage<ReservationVO> listReservationsByMember(Long memberId, int page, int size);
    IPage<ReservationVO> listReservationsByCoach(Long coachId, int page, int size);
    
    /**
     * 根据会员ID和课程ID查询预约记录
     */
    Reservation getReservationByMemberAndCourse(Long memberId, Long courseId);
    
    /**
     * 获取剩余支付时间（秒）
     */
    long getRemainingPaymentSeconds(Long reservationId);
}
