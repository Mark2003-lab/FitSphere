package com.example.gym.controller;

import com.example.gym.common.Response;
import com.example.gym.entity.PrivateCoachingCoupon;
import com.example.gym.entity.User;
import com.example.gym.service.PrivateCoachingCouponService;
import com.example.gym.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 私教兑换券控制器
 */
@RestController
@RequestMapping("/api/coupon")
public class PrivateCoachingCouponController {
    
    private static final Logger log = LoggerFactory.getLogger(PrivateCoachingCouponController.class);

    @Autowired
    private PrivateCoachingCouponService couponService;
    
    @Autowired
    private UserService userService;

    /**
     * 查询用户未使用的兑换券数量
     */
    @GetMapping("/count/{userId}")
    public Response<Integer> getUnusedCount(@PathVariable Long userId) {
        int count = couponService.getUnusedCount(userId);
        return Response.success(count);
    }

    /**
     * 查询用户未使用的兑换券列表
     */
    @GetMapping("/list/{userId}")
    public Response<List<PrivateCoachingCoupon>> getUnusedCoupons(@PathVariable Long userId) {
        List<PrivateCoachingCoupon> coupons = couponService.getUnusedCoupons(userId);
        return Response.success(coupons);
    }

    /**
     * 使用兑换券
     */
    @PostMapping("/use")
    public Response<Boolean> useCoupon(@RequestParam Long userId, @RequestParam Long couponId) {
        boolean result = couponService.useCoupon(userId, couponId);
        return Response.success(result);
    }
    
    /**
     * 查询当前会员的可用兑换券
     */
    @GetMapping("/my-available")
    public Response<List<PrivateCoachingCoupon>> getMyAvailableCoupons() {
        Long userId = getCurrentUserId();
        log.info("/my-available called, userId={}", userId);
        List<PrivateCoachingCoupon> coupons = couponService.getUnusedCoupons(userId);
        return Response.success(coupons);
    }
    
    /**
     * 查询当前会员的所有兑换券
     */
    @GetMapping("/my-coupons")
    public Response<List<PrivateCoachingCoupon>> getMyCoupons() {
        Long userId = getCurrentUserId();
        log.info("/my-coupons called, userId={}", userId);
        List<PrivateCoachingCoupon> coupons = couponService.getUserCoupons(userId);
        return Response.success(coupons);
    }
    
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("getCurrentUserId, username from SecurityContext: {}", username);
        User user = userService.findByUsername(username);
        if (user != null) {
            log.info("Found user: id={}, role={}", user.getId(), user.getRole());
            return user.getId();
        }
        log.warn("User not found for username: {}", username);
        return null;
    }
}