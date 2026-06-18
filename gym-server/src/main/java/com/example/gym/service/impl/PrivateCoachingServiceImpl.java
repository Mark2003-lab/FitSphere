package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.PrivateCoachingDTO;
import com.example.gym.dto.PrivateCoachingVO;
import com.example.gym.entity.Coach;
import com.example.gym.entity.CoachSchedule;
import com.example.gym.entity.Member;
import com.example.gym.entity.PrivateCoaching;
import com.example.gym.entity.PrivateCoachingCoupon;
import com.example.gym.entity.User;
import com.example.gym.exception.BusinessException;
import com.example.gym.mapper.CoachMapper;
import com.example.gym.mapper.CoachScheduleMapper;
import com.example.gym.mapper.MemberMapper;
import com.example.gym.mapper.PrivateCoachingCouponMapper;
import com.example.gym.mapper.PrivateCoachingMapper;
import com.example.gym.mapper.UserMapper;
import com.example.gym.service .PrivateCoachingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PrivateCoachingServiceImpl implements PrivateCoachingService {

    private final PrivateCoachingMapper privateCoachingMapper;
    private final CoachMapper coachMapper;
    private final MemberMapper memberMapper;
    private final CoachScheduleMapper coachScheduleMapper;
    private final PrivateCoachingCouponMapper couponMapper;
    private final UserMapper userMapper;

    public PrivateCoachingServiceImpl(PrivateCoachingMapper privateCoachingMapper,
                                       CoachMapper coachMapper,
                                       MemberMapper memberMapper,
                                       CoachScheduleMapper coachScheduleMapper,
                                       PrivateCoachingCouponMapper couponMapper,
                                       UserMapper userMapper) {
        this.privateCoachingMapper = privateCoachingMapper;
        this.coachMapper = coachMapper;
        this.memberMapper = memberMapper;
        this.coachScheduleMapper = coachScheduleMapper;
        this.couponMapper = couponMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public PrivateCoaching createBooking(Long memberId, PrivateCoachingDTO dto) {
        Coach coach = coachMapper.selectById(dto.getCoachId());
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }

        if (dto.getScheduledTime() == null || dto.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("预约时间无效，请选择未来时间");
        }

        // 检查该时间段教练是否已有私教课
        long conflictCount = privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getCoachId, dto.getCoachId())
                .eq(PrivateCoaching::getScheduledTime, dto.getScheduledTime())
                .in(PrivateCoaching::getStatus, "PENDING", "APPROVED", "CONFIRMED")
        );
        if (conflictCount > 0) {
            throw new BusinessException("该时间段教练已有预约，请选择其他时间");
        }

        // 检查会员是否已有同时段预约
        long memberConflict = privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getMemberId, memberId)
                .eq(PrivateCoaching::getScheduledTime, dto.getScheduledTime())
                .in(PrivateCoaching::getStatus, "PENDING", "APPROVED", "CONFIRMED")
        );
        if (memberConflict > 0) {
            throw new BusinessException("该时间段您已有私教预约");
        }

        PrivateCoaching booking = new PrivateCoaching();
        booking.setMemberId(memberId);
        booking.setCoachId(dto.getCoachId());
        booking.setCourseName(dto.getCourseName() != null ? dto.getCourseName() : "私教课程");
        booking.setCourseType(dto.getCourseType() != null ? dto.getCourseType() : "GENERAL");
        booking.setScheduledTime(dto.getScheduledTime());
        booking.setDuration(dto.getDuration() != null ? dto.getDuration() : 60);
        // 使用教练个人定价
        booking.setPrice(coach.getPrice() != null ? coach.getPrice() : BigDecimal.ZERO);
        booking.setLocation(dto.getLocation() != null ? dto.getLocation() : "健身房私教区");
        booking.setNotes(dto.getNotes());
        booking.setStatus("PENDING");
        booking.setPaymentStatus("UNPAID");
        booking.setCreateTime(LocalDateTime.now());

        privateCoachingMapper.insert(booking);
        return booking;
    }

    @Override
    @Transactional
    public PrivateCoaching createBookingWithCoupon(Long memberId, PrivateCoachingDTO dto, Long couponId) {
        // 验证兑换券
        PrivateCoachingCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("兑换券不存在");
        }
        if (!memberId.equals(coupon.getUserId())) {
            throw new BusinessException("兑换券不属于当前用户");
        }
        if (coupon.getStatus() != 0) {
            throw new BusinessException("兑换券已使用");
        }
        if (coupon.getExpireTime() != null && coupon.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("兑换券已过期");
        }

        Coach coach = coachMapper.selectById(dto.getCoachId());
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }

        if (dto.getScheduledTime() == null || dto.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("预约时间无效，请选择未来时间");
        }

        // 检查该时间段教练是否已有私教课
        long conflictCount = privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getCoachId, dto.getCoachId())
                .eq(PrivateCoaching::getScheduledTime, dto.getScheduledTime())
                .in(PrivateCoaching::getStatus, "PENDING", "APPROVED", "CONFIRMED")
        );
        if (conflictCount > 0) {
            throw new BusinessException("该时间段教练已有预约，请选择其他时间");
        }

        // 检查会员是否已有同时段预约
        long memberConflict = privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getMemberId, memberId)
                .eq(PrivateCoaching::getScheduledTime, dto.getScheduledTime())
                .in(PrivateCoaching::getStatus, "PENDING", "APPROVED", "CONFIRMED")
        );
        if (memberConflict > 0) {
            throw new BusinessException("该时间段您已有私教预约");
        }

        PrivateCoaching booking = new PrivateCoaching();
        booking.setMemberId(memberId);
        booking.setCoachId(dto.getCoachId());
        booking.setCourseName(dto.getCourseName() != null ? dto.getCourseName() : "私教课程");
        booking.setCourseType(dto.getCourseType() != null ? dto.getCourseType() : "GENERAL");
        booking.setScheduledTime(dto.getScheduledTime());
        booking.setDuration(dto.getDuration() != null ? dto.getDuration() : 60);
        // 使用兑换券，价格为0
        booking.setPrice(BigDecimal.ZERO);
        booking.setCouponId(couponId);
        booking.setLocation(dto.getLocation() != null ? dto.getLocation() : "健身房私教区");
        booking.setNotes(dto.getNotes());
        booking.setStatus("PENDING");
        booking.setPaymentStatus("PAID"); // 兑换券支付视为已支付
        booking.setCreateTime(LocalDateTime.now());

        privateCoachingMapper.insert(booking);
        return booking;
    }

    @Override
    @Transactional
    public void approveBooking(Long id) {
        PrivateCoaching booking = privateCoachingMapper.selectById(id);
        if (booking == null) throw new BusinessException("预约记录不存在");
        if (!"PENDING".equals(booking.getStatus())) {
            throw new BusinessException("只能审核待审核状态的预约");
        }
        booking.setStatus("APPROVED");
        booking.setAuditTime(LocalDateTime.now());
        privateCoachingMapper.updateById(booking);
    }

    @Override
    @Transactional
    public void rejectBooking(Long id) {
        PrivateCoaching booking = privateCoachingMapper.selectById(id);
        if (booking == null) throw new BusinessException("预约记录不存在");
        if (!"PENDING".equals(booking.getStatus())) {
            throw new BusinessException("只能审核待审核状态的预约");
        }
        booking.setStatus("REJECTED");
        booking.setAuditTime(LocalDateTime.now());
        privateCoachingMapper.updateById(booking);

        // 如果使用了兑换券，恢复兑换券状态（退回）
        if (booking.getCouponId() != null) {
            PrivateCoachingCoupon coupon = couponMapper.selectById(booking.getCouponId());
            if (coupon != null && coupon.getStatus() != 0) {
                coupon.setStatus(0);
                coupon.setUseTime(null);
                couponMapper.updateById(coupon);
            }
        }
    }

    @Override
    @Transactional
    public void confirmBooking(Long id) {
        PrivateCoaching booking = privateCoachingMapper.selectById(id);
        if (booking == null) throw new BusinessException("预约记录不存在");
        if (!"APPROVED".equals(booking.getStatus())) {
            throw new BusinessException("只能确认已通过状态的预约");
        }
        booking.setStatus("CONFIRMED");
        privateCoachingMapper.updateById(booking);
    }

    @Override
    @Transactional
    public void completeBooking(Long id) {
        PrivateCoaching booking = privateCoachingMapper.selectById(id);
        if (booking == null) throw new BusinessException("预约记录不存在");
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new BusinessException("只能标记已确认状态的课程为完成");
        }
        booking.setStatus("COMPLETED");
        booking.setCompleteTime(LocalDateTime.now());
        privateCoachingMapper.updateById(booking);

        // 如果使用了兑换券，标记兑换券为已使用
        if (booking.getCouponId() != null) {
            PrivateCoachingCoupon coupon = couponMapper.selectById(booking.getCouponId());
            if (coupon != null && coupon.getStatus() == 0) {
                coupon.setStatus(1);
                coupon.setUseTime(LocalDateTime.now());
                couponMapper.updateById(coupon);
            }
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        PrivateCoaching booking = privateCoachingMapper.selectById(id);
        if (booking == null) throw new BusinessException("预约记录不存在");
        if ("COMPLETED".equals(booking.getStatus())) {
            throw new BusinessException("已完成的课程不能取消");
        }
        booking.setStatus("CANCELLED");
        privateCoachingMapper.updateById(booking);

        // 如果使用了兑换券，恢复兑换券状态（退回）
        if (booking.getCouponId() != null) {
            PrivateCoachingCoupon coupon = couponMapper.selectById(booking.getCouponId());
            if (coupon != null && coupon.getStatus() != 0) {
                coupon.setStatus(0);
                coupon.setUseTime(null);
                couponMapper.updateById(coupon);
            }
        }
    }

    @Override
    @Transactional
    public void reviewBooking(Long id, Integer rating, String review) {
        PrivateCoaching booking = privateCoachingMapper.selectById(id);
        if (booking == null) throw new BusinessException("预约记录不存在");
        if (!"COMPLETED".equals(booking.getStatus())) {
            throw new BusinessException("只能评价已完成的课程");
        }
        if (booking.getRating() != null) {
            throw new BusinessException("该课程已评价，不可重复评价");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        booking.setRating(rating);
        booking.setReview(review);
        privateCoachingMapper.updateById(booking);

        // 更新教练评分统计
        updateCoachRating(booking.getCoachId());
    }

    @Override
    @Transactional
    public void payBooking(Long id) {
        PrivateCoaching booking = privateCoachingMapper.selectById(id);
        if (booking == null) throw new BusinessException("预约记录不存在");
        if (!"APPROVED".equals(booking.getStatus()) && !"CONFIRMED".equals(booking.getStatus())) {
            throw new BusinessException("只能支付已通过或已确认的预约");
        }
        if ("PAID".equals(booking.getPaymentStatus())) {
            throw new BusinessException("该预约已支付");
        }
        booking.setPaymentStatus("PAID");
        privateCoachingMapper.updateById(booking);
    }

    @Override
    public PrivateCoaching getById(Long id) {
        return privateCoachingMapper.selectById(id);
    }

    @Override
    public IPage<PrivateCoachingVO> listAll(int page, int size, String status) {
        Page<PrivateCoaching> pageParam = new Page<>(page, size);
        var wrapper = Wrappers.<PrivateCoaching>lambdaQuery()
                .orderByDesc(PrivateCoaching::getCreateTime);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PrivateCoaching::getStatus, status);
        }
        IPage<PrivateCoaching> entityPage = privateCoachingMapper.selectPage(pageParam, wrapper);
        return convertToVO(entityPage);
    }

    @Override
    public IPage<PrivateCoachingVO> listByMember(Long memberId, int page, int size) {
        IPage<PrivateCoaching> entityPage = privateCoachingMapper.selectPage(
            new Page<>(page, size),
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getMemberId, memberId)
                .orderByDesc(PrivateCoaching::getCreateTime)
        );
        return convertToVO(entityPage);
    }

    @Override
    public IPage<PrivateCoachingVO> listByCoach(Long coachId, int page, int size) {
        IPage<PrivateCoaching> entityPage = privateCoachingMapper.selectPage(
            new Page<>(page, size),
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getCoachId, coachId)
                .orderByDesc(PrivateCoaching::getScheduledTime)
        );
        return convertToVO(entityPage);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalBookings = privateCoachingMapper.selectCount(Wrappers.emptyWrapper());
        long pendingBookings = privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getStatus, "PENDING"));
        long completedBookings = privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getStatus, "COMPLETED"));
        long cancelledBookings = privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getStatus, "CANCELLED"));

        // 统计总收入
        List<PrivateCoaching> paidBookings = privateCoachingMapper.selectList(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getPaymentStatus, "PAID"));
        BigDecimal totalRevenue = paidBookings.stream()
            .map(PrivateCoaching::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 统计教练授课数
        List<PrivateCoaching> completedList = privateCoachingMapper.selectList(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getStatus, "COMPLETED"));
        Map<Long, Long> coachSessionCount = completedList.stream()
            .collect(Collectors.groupingBy(PrivateCoaching::getCoachId, Collectors.counting()));

        stats.put("totalBookings", totalBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("completedBookings", completedBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("totalRevenue", totalRevenue);
        stats.put("coachSessionCount", coachSessionCount);

        return stats;
    }

    // ========== 私有辅助方法 ==========

    private void updateCoachRating(Long coachId) {
        List<PrivateCoaching> reviews = privateCoachingMapper.selectList(
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getCoachId, coachId)
                .isNotNull(PrivateCoaching::getRating)
        );
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                .mapToInt(PrivateCoaching::getRating)
                .average()
                .orElse(5.0);
            Coach coach = coachMapper.selectById(coachId);
            if (coach != null) {
                coach.setRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
                coach.setTotalReviews(reviews.size());
                coachMapper.updateById(coach);
            }
        }
    }

    private IPage<PrivateCoachingVO> convertToVO(IPage<PrivateCoaching> entityPage) {
        List<PrivateCoaching> records = entityPage.getRecords();

        List<Long> memberIds = records.stream().map(PrivateCoaching::getMemberId).distinct().collect(Collectors.toList());
        List<Long> coachIds = records.stream().map(PrivateCoaching::getCoachId).distinct().collect(Collectors.toList());

        Map<Long, Member> memberMap = memberIds.isEmpty() ? Map.of() :
            memberMapper.selectList(Wrappers.<Member>lambdaQuery().in(Member::getId, memberIds))
                .stream().collect(Collectors.toMap(Member::getId, m -> m));

        Map<Long, Coach> coachMap = coachIds.isEmpty() ? Map.of() :
            coachMapper.selectList(Wrappers.<Coach>lambdaQuery().in(Coach::getId, coachIds))
                .stream().collect(Collectors.toMap(Coach::getId, c -> c));

        // 始终从用户表获取最新头像，确保教练修改头像后实时生效
        Map<Long, String> coachUserAvatars = Map.of();
        if (!coachMap.isEmpty()) {
            Set<Long> userIdsForAvatar = coachMap.values().stream()
                    .map(Coach::getUserId)
                    .filter(uid -> uid != null)
                    .collect(Collectors.toSet());
            if (!userIdsForAvatar.isEmpty()) {
                coachUserAvatars = userMapper.selectBatchIds(userIdsForAvatar).stream()
                        .filter(u -> u.getAvatar() != null && !u.getAvatar().isEmpty())
                        .collect(Collectors.toMap(User::getId, User::getAvatar));
            }
        }
        final Map<Long, String> finalCoachUserAvatars = coachUserAvatars;

        List<PrivateCoachingVO> voList = records.stream().map(entity -> {
            PrivateCoachingVO vo = new PrivateCoachingVO();
            vo.setId(entity.getId());
            vo.setMemberId(entity.getMemberId());
            vo.setCoachId(entity.getCoachId());
            vo.setCourseName(entity.getCourseName());
            vo.setCourseType(entity.getCourseType());
            vo.setScheduledTime(entity.getScheduledTime());
            vo.setDuration(entity.getDuration());
            vo.setPrice(entity.getPrice());
            vo.setStatus(entity.getStatus());
            vo.setPaymentStatus(entity.getPaymentStatus());
            vo.setCouponId(entity.getCouponId());
            vo.setLocation(entity.getLocation());
            vo.setNotes(entity.getNotes());
            vo.setCoachNotes(entity.getCoachNotes());
            vo.setRating(entity.getRating());
            vo.setReview(entity.getReview());
            vo.setCreateTime(entity.getCreateTime());
            vo.setAuditTime(entity.getAuditTime());
            vo.setCompleteTime(entity.getCompleteTime());

            Member member = memberMap.get(entity.getMemberId());
            if (member != null) {
                vo.setMemberName(member.getName());
                vo.setMemberPhone(member.getPhone());
            }

            Coach coach = coachMap.get(entity.getCoachId());
            if (coach != null) {
                vo.setCoachName(coach.getName());
                // 始终优先使用用户表的最新头像
                String coachAvatar = finalCoachUserAvatars.getOrDefault(coach.getUserId(), coach.getAvatar());
                vo.setCoachAvatar(coachAvatar);
                vo.setCoachSpeciality(coach.getSpeciality());
            }

            return vo;
        }).collect(Collectors.toList());

        IPage<PrivateCoachingVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(entityPage.getTotal());
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        return voPage;
    }
}
