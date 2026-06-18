package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.dto.PrivateCoachingDTO;
import com.example.gym.dto.PrivateCoachingVO;
import com.example.gym.entity.Coach;
import com.example.gym.entity.Member;
import com.example.gym.entity.PrivateCoaching;
import com.example.gym.entity.User;
import com.example.gym.service.CoachService;
import com.example.gym.service.MemberService;
import com.example.gym.service.PrivateCoachingService;
import com.example.gym.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/private-coaching")
public class PrivateCoachingController {

    private final PrivateCoachingService privateCoachingService;
    private final UserService userService;
    private final MemberService memberService;
    private final CoachService coachService;

    public PrivateCoachingController(PrivateCoachingService privateCoachingService,
                                      UserService userService,
                                      MemberService memberService,
                                      CoachService coachService) {
        this.privateCoachingService = privateCoachingService;
        this.userService = userService;
        this.memberService = memberService;
        this.coachService = coachService;
    }

    // ========== 会员端接口 ==========

    /** 会员提交私教预约 */
    @PostMapping
    public Response<PrivateCoaching> createBooking(@RequestBody PrivateCoachingDTO dto) {
        Long memberId = getCurrentMemberId();
        PrivateCoaching booking = privateCoachingService.createBooking(memberId, dto);
        return Response.success("预约提交成功，等待管理员审核", booking);
    }

    /** 会员使用兑换券提交私教预约 */
    @PostMapping("/coupon")
    public Response<PrivateCoaching> createBookingWithCoupon(@RequestBody PrivateCoachingDTO dto, @RequestParam Long couponId) {
        Long memberId = getCurrentMemberId();
        PrivateCoaching booking = privateCoachingService.createBookingWithCoupon(memberId, dto, couponId);
        return Response.success("使用兑换券预约成功，等待管理员审核", booking);
    }

    /** 会员查看自己的私教预约记录 */
    @GetMapping("/my")
    public Response<IPage<PrivateCoachingVO>> listMyBookings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long memberId = getCurrentMemberId();
        IPage<PrivateCoachingVO> result = privateCoachingService.listByMember(memberId, page, size);
        return Response.success(result);
    }

    /** 会员取消预约 */
    @PutMapping("/{id}/cancel")
    public Response<Void> cancelBooking(@PathVariable Long id) {
        privateCoachingService.cancelBooking(id);
        return Response.success("取消成功", null);
    }

    /** 会员支付 */
    @PutMapping("/{id}/pay")
    public Response<Void> payBooking(@PathVariable Long id) {
        privateCoachingService.payBooking(id);
        return Response.success("支付成功", null);
    }

    /** 会员评价教练 */
    @PutMapping("/{id}/review")
    public Response<Void> reviewBooking(@PathVariable Long id,
                                         @RequestParam Integer rating,
                                         @RequestParam(required = false) String review) {
        privateCoachingService.reviewBooking(id, rating, review);
        return Response.success("评价成功", null);
    }

    // ========== 管理员端接口 ==========

    /** 管理员查看所有私教预约 */
    @GetMapping("/admin/list")
    public Response<IPage<PrivateCoachingVO>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        IPage<PrivateCoachingVO> result = privateCoachingService.listAll(page, size, status);
        return Response.success(result);
    }

    /** 管理员审核通过 */
    @PutMapping("/{id}/approve")
    public Response<Void> approveBooking(@PathVariable Long id) {
        privateCoachingService.approveBooking(id);
        return Response.success("审核通过", null);
    }

    /** 管理员审核拒绝 */
    @PutMapping("/{id}/reject")
    public Response<Void> rejectBooking(@PathVariable Long id) {
        privateCoachingService.rejectBooking(id);
        return Response.success("已拒绝", null);
    }

    /** 管理员查看统计数据 */
    @GetMapping("/statistics")
    public Response<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = privateCoachingService.getStatistics();
        return Response.success(stats);
    }

    // ========== 教练端接口 ==========

    /** 教练查看自己的私教课程 */
    @GetMapping("/coach")
    public Response<IPage<PrivateCoachingVO>> listCoachBookings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long coachId = getCurrentCoachId();
        IPage<PrivateCoachingVO> result = privateCoachingService.listByCoach(coachId, page, size);
        return Response.success(result);
    }

    /** 教练确认接受 */
    @PutMapping("/{id}/confirm")
    public Response<Void> confirmBooking(@PathVariable Long id) {
        privateCoachingService.confirmBooking(id);
        return Response.success("已确认", null);
    }

    /** 教练标记完成 */
    @PutMapping("/{id}/complete")
    public Response<Void> completeBooking(@PathVariable Long id) {
        privateCoachingService.completeBooking(id);
        return Response.success("已标记完成", null);
    }

    /** 教练添加备注 */
    @PutMapping("/{id}/coach-notes")
    public Response<Void> updateCoachNotes(@PathVariable Long id, @RequestBody Map<String, String> body) {
        PrivateCoaching booking = privateCoachingService.getById(id);
        if (booking == null) return Response.error("预约记录不存在");
        booking.setCoachNotes(body.get("coachNotes"));
        return Response.success("备注更新成功", null);
    }

    // ========== 辅助方法 ==========

    private Long getCurrentMemberId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) return null;
        Member member = memberService.getMemberById(user.getId());
        if (member != null) return member.getId();
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            Member phoneMember = memberService.getMemberByPhone(user.getPhone());
            if (phoneMember != null) return phoneMember.getId();
        }
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

    private Long getCurrentCoachId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) return null;
        Coach coach = coachService.getCoachById(user.getId());
        if (coach != null) return coach.getId();
        // 按手机号查找
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            Coach phoneCoach = coachService.getCoachByPhone(user.getPhone());
            if (phoneCoach != null) return phoneCoach.getId();
        }
        return null;
    }
}
