package com.example.gym.controller;

import com.example.gym.common.Response;
import com.example.gym.dto.CoachScheduleDTO;
import com.example.gym.entity.Coach;
import com.example.gym.entity.CoachSchedule;
import com.example.gym.entity.User;
import com.example.gym.service.CoachScheduleService;
import com.example.gym.service.CoachService;
import com.example.gym.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/coach-schedule")
public class CoachScheduleController {

    private final CoachScheduleService coachScheduleService;
    private final UserService userService;
    private final CoachService coachService;

    public CoachScheduleController(CoachScheduleService coachScheduleService,
                                    UserService userService,
                                    CoachService coachService) {
        this.coachScheduleService = coachScheduleService;
        this.userService = userService;
        this.coachService = coachService;
    }

    /** 教练创建单个可用时间段 */
    @PostMapping
    public Response<CoachSchedule> createSchedule(@RequestBody CoachScheduleDTO dto) {
        dto.setCoachId(getCurrentCoachId());
        CoachSchedule schedule = coachScheduleService.createSchedule(dto);
        return Response.success("创建成功", schedule);
    }

    /** 教练批量创建可用时间段 */
    @PostMapping("/batch")
    public Response<List<CoachSchedule>> batchCreateSchedule(
            @RequestBody CoachScheduleDTO dto,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        dto.setCoachId(getCurrentCoachId());
        List<CoachSchedule> schedules = coachScheduleService.batchCreateSchedule(dto, endDate);
        return Response.success("批量创建成功", schedules);
    }

    /** 删除时间段 */
    @DeleteMapping("/{id}")
    public Response<Void> deleteSchedule(@PathVariable Long id) {
        coachScheduleService.deleteSchedule(id);
        return Response.success("删除成功", null);
    }

    /** 查看教练指定日期的可用时间 */
    @GetMapping("/coach/{coachId}")
    public Response<List<CoachSchedule>> listByCoachAndDate(
            @PathVariable Long coachId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CoachSchedule> schedules = coachScheduleService.listByCoachAndDate(coachId, date);
        return Response.success(schedules);
    }

    /** 查看教练日期范围内的可用时间（会员查看用） */
    @GetMapping("/coach/{coachId}/range")
    public Response<List<CoachSchedule>> listByCoachAndDateRange(
            @PathVariable Long coachId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CoachSchedule> schedules = coachScheduleService.listByCoachAndDateRange(coachId, startDate, endDate);
        return Response.success(schedules);
    }

    /** 教练查看自己所有可用时间 */
    @GetMapping("/my")
    public Response<List<CoachSchedule>> listMySchedule() {
        Long coachId = getCurrentCoachId();
        List<CoachSchedule> schedules = coachScheduleService.listByCoach(coachId);
        return Response.success(schedules);
    }

    /** 设置时间段是否可用 */
    @PutMapping("/{id}/availability")
    public Response<Void> setAvailability(@PathVariable Long id, @RequestParam Boolean available) {
        coachScheduleService.setAvailability(id, available);
        return Response.success("更新成功", null);
    }

    private Long getCurrentCoachId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) return null;
        Coach coach = coachService.getCoachById(user.getId());
        if (coach != null) return coach.getId();
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            Coach phoneCoach = coachService.getCoachByPhone(user.getPhone());
            if (phoneCoach != null) return phoneCoach.getId();
        }
        return null;
    }
}
