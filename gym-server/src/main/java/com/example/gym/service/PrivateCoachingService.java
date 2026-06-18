package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.dto.PrivateCoachingDTO;
import com.example.gym.dto.PrivateCoachingVO;
import com.example.gym.entity.PrivateCoaching;

import java.util.Map;

public interface PrivateCoachingService {

    /** 会员提交私教预约 */
    PrivateCoaching createBooking(Long memberId, PrivateCoachingDTO dto);

    /** 会员使用兑换券提交私教预约 */
    PrivateCoaching createBookingWithCoupon(Long memberId, PrivateCoachingDTO dto, Long couponId);

    /** 管理员审核通过 */
    void approveBooking(Long id);

    /** 管理员审核拒绝 */
    void rejectBooking(Long id);

    /** 教练确认接受 */
    void confirmBooking(Long id);

    /** 教练标记完成 */
    void completeBooking(Long id);

    /** 会员取消预约 */
    void cancelBooking(Long id);

    /** 会员评价 */
    void reviewBooking(Long id, Integer rating, String review);

    /** 会员支付 */
    void payBooking(Long id);

    /** 查询单条记录 */
    PrivateCoaching getById(Long id);

    /** 管理员：查询所有私教预约（分页） */
    IPage<PrivateCoachingVO> listAll(int page, int size, String status);

    /** 会员：查询自己的预约记录 */
    IPage<PrivateCoachingVO> listByMember(Long memberId, int page, int size);

    /** 教练：查询自己的私教课程 */
    IPage<PrivateCoachingVO> listByCoach(Long coachId, int page, int size);

    /** 管理员统计：私教课程数据 */
    Map<String, Object> getStatistics();
}
