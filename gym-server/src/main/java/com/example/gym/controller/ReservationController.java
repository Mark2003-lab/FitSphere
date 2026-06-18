package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.dto.ReservationVO;
import com.example.gym.entity.Reservation;
import com.example.gym.entity.User;
import com.example.gym.service.MemberService;
import com.example.gym.service.ReservationService;
import com.example.gym.service.UserService;
import com.example.gym.service.impl.ReservationServiceImpl;
import com.example.gym.entity.Member;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    
    private final ReservationService reservationService;
    private final ReservationServiceImpl reservationServiceImpl;
    private final UserService userService;
    private final MemberService memberService;
    
    public ReservationController(ReservationService reservationService, ReservationServiceImpl reservationServiceImpl, UserService userService, MemberService memberService) {
        this.reservationService = reservationService;
        this.reservationServiceImpl = reservationServiceImpl;
        this.userService = userService;
        this.memberService = memberService;
    }
    
    @PostMapping
    public Response<Reservation> createReservation(@RequestParam Long courseId) {
        Long memberId = getCurrentMemberId();
        Reservation reservation = reservationService.createReservation(memberId, courseId);
        return Response.success("预约成功", reservation);
    }
    
    @GetMapping("/create")
    public Response<Reservation> createReservationGet(@RequestParam Long courseId) {
        Long memberId = getCurrentMemberId();
        Reservation reservation = reservationService.createReservation(memberId, courseId);
        return Response.success("预约成功", reservation);
    }
    
    @PutMapping("/{id}/cancel")
    public Response<Void> cancelReservation(@PathVariable Long id) {
        Long userId = getCurrentMemberId();
        boolean isMember = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> "ROLE_MEMBER".equals(authority.getAuthority()));
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return Response.error(404, "预约不存在");
        }
        if (isMember && !reservation.getMemberId().equals(userId)) {
            return Response.error(403, "只能取消自己的预约");
        }
        reservationService.cancelReservation(id);
        return Response.success("取消成功", null);
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteReservation(@PathVariable Long id) {
        Long userId = getCurrentMemberId();
        boolean isMember = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> "ROLE_MEMBER".equals(authority.getAuthority()));
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return Response.error(404, "预约不存在");
        }
        if (isMember && !reservation.getMemberId().equals(userId)) {
            return Response.error(403, "只能删除自己的预约");
        }
        reservationService.deleteReservation(id);
        return Response.success("删除成功", null);
    }
    
    @PutMapping("/{id}/approve")
    public Response<Void> approveReservation(@PathVariable Long id) {
        reservationService.approveReservation(id);
        return Response.success("审核通过", null);
    }
    
    @PutMapping("/{id}/reject")
    public Response<Void> rejectReservation(@PathVariable Long id) {
        reservationService.rejectReservation(id);
        return Response.success("已拒绝", null);
    }
    
    @PutMapping("/{id}/pay")
    public Response<Void> payReservation(@PathVariable Long id) {
        reservationService.payReservation(id);
        return Response.success("支付成功", null);
    }
    
    @GetMapping("/{id}/remaining-time")
    public Response<Long> getRemainingPaymentTime(@PathVariable Long id) {
        long remainingSeconds = reservationService.getRemainingPaymentSeconds(id);
        return Response.success(remainingSeconds);
    }
    
    @GetMapping("/{id}")
    public Response<Reservation> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return Response.success(reservation);
    }
    
    @GetMapping
    public Response<IPage<ReservationVO>> listReservations(@RequestParam(defaultValue = "1") int page, 
                                                            @RequestParam(defaultValue = "10") int size) {
        IPage<ReservationVO> reservations = reservationServiceImpl.listReservationsWithDetails(page, size);
        return Response.success(reservations);
    }
    
    @GetMapping("/my")
    public Response<IPage<ReservationVO>> listMyReservations(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentMemberId();
        IPage<ReservationVO> reservations = reservationServiceImpl.listReservationsByMember(userId, page, size);
        return Response.success(reservations);
    }
    
    @GetMapping("/coach")
    public Response<IPage<ReservationVO>> listCoachReservations(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Long coachId = user != null ? user.getId() : null;
        IPage<ReservationVO> reservations = reservationServiceImpl.listReservationsByCoach(coachId, page, size);
        return Response.success(reservations);
    }
    
    /**
     * 获取当前会员的 member ID（与 CheckinController 一致的策略）
     * 先按 ID 查，再按手机号查，都不存在则自动创建
     */
    private Long getCurrentMemberId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) return null;
        // 1. 按 user.id 查找
        Member member = memberService.getMemberById(user.getId());
        if (member != null) return member.getId();
        // 2. 按手机号查找（处理 ID 不一致的历史数据）
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            Member phoneMember = memberService.getMemberByPhone(user.getPhone());
            if (phoneMember != null) return phoneMember.getId();
        }
        // 3. 都不存在，自动创建
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
