package com.example.gym.service;

import com.example.gym.entity.PrivateCoachingCoupon;

import java.util.List;

/**
 * 私教兑换券服务接口
 */
public interface PrivateCoachingCouponService {

    /**
     * 为用户发放兑换券
     */
    void grantCoupons(Long userId, int count);

    /**
     * 查询用户未使用的兑换券数量
     */
    int getUnusedCount(Long userId);

    /**
     * 查询用户未使用的兑换券列表
     */
    List<PrivateCoachingCoupon> getUnusedCoupons(Long userId);
    
    /**
     * 查询用户所有兑换券
     */
    List<PrivateCoachingCoupon> getUserCoupons(Long userId);

    /**
     * 使用兑换券
     */
    boolean useCoupon(Long userId, Long couponId);

    /**
     * 检查用户是否为首充
     */
    boolean isFirstPurchase(Long userId);
}