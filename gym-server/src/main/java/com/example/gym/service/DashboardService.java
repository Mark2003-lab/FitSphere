package com.example.gym.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.gym.entity.*;
import com.example.gym.mapper.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MemberMapper memberMapper;
    private final CoachMapper coachMapper;
    private final CourseMapper courseMapper;
    private final CheckinMapper checkinMapper;
    private final ReservationMapper reservationMapper;
    private final PrivateCoachingMapper privateCoachingMapper;

    public DashboardService(MemberMapper memberMapper, CoachMapper coachMapper, CourseMapper courseMapper,
                           CheckinMapper checkinMapper, ReservationMapper reservationMapper,
                           PrivateCoachingMapper privateCoachingMapper) {
        this.memberMapper = memberMapper;
        this.coachMapper = coachMapper;
        this.courseMapper = courseMapper;
        this.checkinMapper = checkinMapper;
        this.reservationMapper = reservationMapper;
        this.privateCoachingMapper = privateCoachingMapper;
    }

    /* ========== 管理员 ========== */

    public long countMembers() { return memberMapper.selectCount(null); }
    public long countCoaches() { return coachMapper.selectCount(null); }
    public long countCourses() { return courseMapper.selectCount(null); }
    public long countTodayCheckins() { return checkinMapper.countTodayCheckins(); }

    public List<Map<String, Object>> getWeeklyCheckin() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String[] weekNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            long count = checkinMapper.selectCount(
                Wrappers.<Checkin>lambdaQuery()
                    .ge(Checkin::getCheckinTime, date.atStartOfDay())
                    .lt(Checkin::getCheckinTime, date.plusDays(1).atStartOfDay())
            );
            Map<String, Object> day = new HashMap<>();
            day.put("name", weekNames[i]);
            day.put("value", (int) count);
            result.add(day);
        }
        return result;
    }

    public List<Map<String, Object>> getCourseReservations() {
        List<Course> courses = courseMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            long count = reservationMapper.selectCount(
                Wrappers.<Reservation>lambdaQuery().eq(Reservation::getCourseId, c.getId())
            );
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", c.getCourseName());
                item.put("value", (int) count);
                result.add(item);
            }
        }
        return result;
    }

    public List<Checkin> getRecentCheckins(int limit) {
        return checkinMapper.selectList(
            Wrappers.<Checkin>lambdaQuery()
                .orderByDesc(Checkin::getCheckinTime)
                .last("LIMIT " + limit)
        );
    }

    /** 获取资金流水统计（课程流水、私教流水、总收入） */
    public Map<String, Object> getFinanceStats() {
        Map<String, Object> result = new HashMap<>();
        
        // 课程流水：统计已支付的预约订单总金额
        BigDecimal courseRevenue = reservationMapper.selectList(
            Wrappers.<Reservation>lambdaQuery().eq(Reservation::getPaymentStatus, "PAID")
        ).stream()
            .map(r -> {
                Course c = courseMapper.selectById(r.getCourseId());
                return c != null && c.getPrice() != null ? c.getPrice() : BigDecimal.ZERO;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("courseRevenue", courseRevenue);
        
        // 私教流水：统计已支付的私教订单总金额
        BigDecimal privateCoachingRevenue = privateCoachingMapper.selectList(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getPaymentStatus, "PAID")
        ).stream()
            .map(pc -> pc.getPrice() != null ? pc.getPrice() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("privateCoachingRevenue", privateCoachingRevenue);
        
        // 总收入
        result.put("totalRevenue", courseRevenue.add(privateCoachingRevenue));
        
        // 本月课程流水
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        BigDecimal monthlyCourseRevenue = reservationMapper.selectList(
            Wrappers.<Reservation>lambdaQuery()
                .eq(Reservation::getPaymentStatus, "PAID")
                .ge(Reservation::getReservationTime, startOfMonth.atStartOfDay())
        ).stream()
            .map(r -> {
                Course c = courseMapper.selectById(r.getCourseId());
                return c != null && c.getPrice() != null ? c.getPrice() : BigDecimal.ZERO;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("monthlyCourseRevenue", monthlyCourseRevenue);
        
        // 本月私教流水
        BigDecimal monthlyPrivateRevenue = privateCoachingMapper.selectList(
            Wrappers.<PrivateCoaching>lambdaQuery()
                .eq(PrivateCoaching::getPaymentStatus, "PAID")
                .ge(PrivateCoaching::getCreateTime, startOfMonth.atStartOfDay())
        ).stream()
            .map(pc -> pc.getPrice() != null ? pc.getPrice() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("monthlyPrivateRevenue", monthlyPrivateRevenue);
        
        // 本月总收入
        result.put("monthlyTotalRevenue", monthlyCourseRevenue.add(monthlyPrivateRevenue));
        
        return result;
    }

    /* ========== 会员 ========== */

    public long countReservationsByMember(Long memberId) {
        return reservationMapper.selectCount(
            Wrappers.<Reservation>lambdaQuery().eq(Reservation::getMemberId, memberId));
    }

    public List<Reservation> listRecentReservations(Long memberId, int limit) {
        return reservationMapper.selectList(
            Wrappers.<Reservation>lambdaQuery()
                .eq(Reservation::getMemberId, memberId)
                .orderByDesc(Reservation::getReservationTime)
                .last("LIMIT " + limit));
    }

    public long countCheckinsByMember(Long memberId) {
        return checkinMapper.selectCount(
            Wrappers.<Checkin>lambdaQuery().eq(Checkin::getMemberId, memberId));
    }

    public List<Checkin> listRecentCheckinsByMember(Long memberId, int limit) {
        return checkinMapper.selectList(
            Wrappers.<Checkin>lambdaQuery()
                .eq(Checkin::getMemberId, memberId)
                .orderByDesc(Checkin::getCheckinTime)
                .last("LIMIT " + limit));
    }

    public long countPrivateCoachingByMember(Long memberId) {
        return privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getMemberId, memberId));
    }

    /* ========== 教练 ========== */

    public long countCoursesByCoach(Long coachId) {
        return courseMapper.selectCount(
            Wrappers.<Course>lambdaQuery().eq(Course::getCoachId, coachId));
    }

    public long countDistinctMembersByCoach(Long coachId) {
        // 通过预约表统计教练的不同会员数
        List<Reservation> reservations = reservationMapper.selectList(
            Wrappers.<Reservation>lambdaQuery()
                .apply("course_id IN (SELECT id FROM course WHERE coach_id = {0})", coachId));
        return reservations.stream().map(Reservation::getMemberId).distinct().count();
    }

    public List<Map<String, Object>> listTodayCoursesByCoach(Long coachId) {
        LocalDate today = LocalDate.now();
        List<Course> courses = courseMapper.selectList(
            Wrappers.<Course>lambdaQuery()
                .eq(Course::getCoachId, coachId)
                .ge(Course::getCourseTime, today.atStartOfDay())
                .lt(Course::getCourseTime, today.plusDays(1).atStartOfDay())
                .orderByAsc(Course::getCourseTime));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            long resCount = reservationMapper.selectCount(
                Wrappers.<Reservation>lambdaQuery().eq(Reservation::getCourseId, c.getId()));
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("courseName", c.getCourseName());
            m.put("time", c.getCourseTime() != null ? c.getCourseTime().toLocalTime().toString() : "");
            m.put("location", c.getLocation() != null ? c.getLocation() : "未设置");
            m.put("memberCount", (int) resCount);
            m.put("capacity", c.getCapacity());
            result.add(m);
        }
        return result;
    }

    public long countTodayReservationsByCoach(Long coachId) {
        LocalDate today = LocalDate.now();
        List<Course> courses = courseMapper.selectList(
            Wrappers.<Course>lambdaQuery().eq(Course::getCoachId, coachId));
        long count = 0;
        for (Course c : courses) {
            count += reservationMapper.selectCount(
                Wrappers.<Reservation>lambdaQuery().eq(Reservation::getCourseId, c.getId()));
        }
        return count;
    }

    public long countPrivateCoachingByCoach(Long coachId) {
        return privateCoachingMapper.selectCount(
            Wrappers.<PrivateCoaching>lambdaQuery().eq(PrivateCoaching::getCoachId, coachId));
    }

    /* ========== 导出数据 ========== */

    /** 获取预约流水（含会员、教练、课程、价格信息） */
    public List<Map<String, Object>> getReservationLedger() {
        List<Reservation> reservations = reservationMapper.selectList(
            Wrappers.<Reservation>lambdaQuery().orderByDesc(Reservation::getReservationTime));

        Set<Long> memberIds = reservations.stream().map(Reservation::getMemberId).collect(Collectors.toSet());
        Set<Long> courseIds = reservations.stream().map(Reservation::getCourseId).collect(Collectors.toSet());

        Map<Long, Member> memberMap = memberIds.isEmpty() ? Map.of() :
            memberMapper.selectBatchIds(memberIds).stream().collect(Collectors.toMap(Member::getId, m -> m));
        Map<Long, Course> courseMap = courseIds.isEmpty() ? Map.of() :
            courseMapper.selectBatchIds(courseIds).stream().collect(Collectors.toMap(Course::getId, c -> c));
        Set<Long> coachIds = courseMap.values().stream().map(Course::getCoachId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Coach> coachMap = coachIds.isEmpty() ? Map.of() :
            coachMapper.selectBatchIds(coachIds).stream().collect(Collectors.toMap(Coach::getId, c -> c));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Reservation r : reservations) {
            Map<String, Object> row = new LinkedHashMap<>();
            Member m = memberMap.get(r.getMemberId());
            Course c = courseMap.get(r.getCourseId());
            Coach co = c != null ? coachMap.get(c.getCoachId()) : null;

            row.put("会员姓名", m != null ? m.getName() : "未知");
            row.put("会员电话", m != null ? m.getPhone() : "");
            row.put("课程名称", c != null ? c.getCourseName() : "未知");
            row.put("教练", co != null ? co.getName() : "未知");
            row.put("价格", c != null ? (c.getPrice() != null ? c.getPrice() : BigDecimal.ZERO) : BigDecimal.ZERO);
            row.put("审核状态", getReservationStatusText(r.getStatus()));
            row.put("支付状态", "PAID".equals(r.getPaymentStatus()) ? "已支付" : "未支付");
            row.put("预约时间", r.getReservationTime() != null ? r.getReservationTime().format(DT_FMT) : "");
            result.add(row);
        }
        return result;
    }

    /** 获取私教流水（含会员、教练、价格、支付状态） */
    public List<Map<String, Object>> getPrivateCoachingLedger() {
        List<PrivateCoaching> list = privateCoachingMapper.selectList(
            Wrappers.<PrivateCoaching>lambdaQuery().orderByDesc(PrivateCoaching::getCreateTime));

        Set<Long> memberIds = list.stream().map(PrivateCoaching::getMemberId).collect(Collectors.toSet());
        Set<Long> coachIds = list.stream().map(PrivateCoaching::getCoachId).collect(Collectors.toSet());

        Map<Long, Member> memberMap = memberIds.isEmpty() ? Map.of() :
            memberMapper.selectBatchIds(memberIds).stream().collect(Collectors.toMap(Member::getId, m -> m));
        Map<Long, Coach> coachMap = coachIds.isEmpty() ? Map.of() :
            coachMapper.selectBatchIds(coachIds).stream().collect(Collectors.toMap(Coach::getId, c -> c));

        List<Map<String, Object>> result = new ArrayList<>();
        for (PrivateCoaching pc : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            Member m = memberMap.get(pc.getMemberId());
            Coach co = coachMap.get(pc.getCoachId());

            row.put("会员姓名", m != null ? m.getName() : "未知");
            row.put("会员电话", m != null ? m.getPhone() : "");
            row.put("教练", co != null ? co.getName() : "未知");
            row.put("课程名称", pc.getCourseName() != null ? pc.getCourseName() : "");
            row.put("价格", pc.getPrice() != null ? pc.getPrice() : BigDecimal.ZERO);
            row.put("支付状态", "PAID".equals(pc.getPaymentStatus()) ? "已支付" : "未支付");
            row.put("预约状态", getCoachingStatusText(pc.getStatus()));
            row.put("预约时间", pc.getScheduledTime() != null ? pc.getScheduledTime().format(DT_FMT) : "");
            row.put("创建时间", pc.getCreateTime() != null ? pc.getCreateTime().format(DT_FMT) : "");
            result.add(row);
        }
        return result;
    }

    private String getReservationStatusText(String s) {
        return switch (s != null ? s : "") {
            case "PENDING" -> "待审核";
            case "APPROVED" -> "已通过";
            case "REJECTED" -> "已拒绝";
            case "CANCELLED" -> "已取消";
            default -> s;
        };
    }

    private String getCoachingStatusText(String s) {
        return switch (s != null ? s : "") {
            case "PENDING" -> "待审核";
            case "APPROVED" -> "已通过";
            case "CONFIRMED" -> "已确认";
            case "COMPLETED" -> "已完成";
            case "CANCELLED" -> "已取消";
            case "REJECTED" -> "已拒绝";
            default -> s;
        };
    }
}
