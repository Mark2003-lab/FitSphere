package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.gym.entity.PrivateCoachingCoupon;
import com.example.gym.entity.UserCard;
import com.example.gym.mapper.PrivateCoachingCouponMapper;
import com.example.gym.mapper.UserCardMapper;
import com.example.gym.service.PrivateCoachingCouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 私教兑换券服务实现类
 */
@Service
public class PrivateCoachingCouponServiceImpl implements PrivateCoachingCouponService {

    private final PrivateCoachingCouponMapper couponMapper;
    private final UserCardMapper userCardMapper;

    public PrivateCoachingCouponServiceImpl(PrivateCoachingCouponMapper couponMapper, UserCardMapper userCardMapper) {
        this.couponMapper = couponMapper;
        this.userCardMapper = userCardMapper;
    }

    @Override
    @Transactional
    public void grantCoupons(Long userId, int count) {
        LocalDateTime expireTime = LocalDateTime.now().plusMonths(3); // 3个月有效期

        for (int i = 0; i < count; i++) {
            PrivateCoachingCoupon coupon = new PrivateCoachingCoupon();
            coupon.setUserId(userId);
            coupon.setCouponCode(generateCouponCode());
            coupon.setStatus(0); // 0-未使用
            coupon.setExpireTime(expireTime);
            coupon.setCreateTime(LocalDateTime.now());
            couponMapper.insert(coupon);
        }
    }

    @Override
    public int getUnusedCount(Long userId) {
        return couponMapper.countUnusedByUserId(userId);
    }

    @Override
    public List<PrivateCoachingCoupon> getUnusedCoupons(Long userId) {
        return couponMapper.selectUnusedByUserId(userId);
    }
    
    @Override
    public List<PrivateCoachingCoupon> getUserCoupons(Long userId) {
        return couponMapper.selectList(
            Wrappers.<PrivateCoachingCoupon>lambdaQuery()
                .eq(PrivateCoachingCoupon::getUserId, userId)
                .orderByDesc(PrivateCoachingCoupon::getCreateTime)
        );
    }

    @Override
    @Transactional
    public boolean useCoupon(Long userId, Long couponId) {
        PrivateCoachingCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 0 || 
            coupon.getUserId().equals(userId) == false ||
            coupon.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        coupon.setStatus(1); // 1-已使用
        coupon.setUseTime(LocalDateTime.now());
        couponMapper.updateById(coupon);
        return true;
    }

    @Override
    public boolean isFirstPurchase(Long userId) {
        Long count = userCardMapper.selectCount(
            Wrappers.<UserCard>lambdaQuery().eq(UserCard::getUserId, userId));
        return count == 0;
    }

    /**
     * 生成兑换券码
     */
    private String generateCouponCode() {
        return "COUPON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}