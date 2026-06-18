package com.example.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gym.entity.PrivateCoachingCoupon;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 私教兑换券Mapper
 */
@Mapper
public interface PrivateCoachingCouponMapper extends BaseMapper<PrivateCoachingCoupon> {

    /**
     * 查询用户未使用的兑换券数量
     */
    int countUnusedByUserId(Long userId);

    /**
     * 查询用户未使用的兑换券列表
     */
    List<PrivateCoachingCoupon> selectUnusedByUserId(Long userId);
}