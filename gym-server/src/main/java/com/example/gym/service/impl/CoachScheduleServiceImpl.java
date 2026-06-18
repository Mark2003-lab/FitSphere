package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.gym.dto.CoachScheduleDTO;
import com.example.gym.entity.CoachSchedule;
import com.example.gym.exception.BusinessException;
import com.example.gym.mapper.CoachScheduleMapper;
import com.example.gym.service.CoachScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoachScheduleServiceImpl implements CoachScheduleService {

    private final CoachScheduleMapper coachScheduleMapper;

    public CoachScheduleServiceImpl(CoachScheduleMapper coachScheduleMapper) {
        this.coachScheduleMapper = coachScheduleMapper;
    }

    @Override
    @Transactional
    public CoachSchedule createSchedule(CoachScheduleDTO dto) {
        if (dto.getCoachId() == null) throw new BusinessException("教练ID不能为空");
        if (dto.getAvailableDate() == null) throw new BusinessException("日期不能为空");
        if (dto.getStartTime() == null || dto.getEndTime() == null) throw new BusinessException("时间不能为空");
        if (dto.getStartTime().isAfter(dto.getEndTime())) throw new BusinessException("开始时间不能晚于结束时间");

        // 检查是否已有相同时间段
        long existing = coachScheduleMapper.selectCount(
            Wrappers.<CoachSchedule>lambdaQuery()
                .eq(CoachSchedule::getCoachId, dto.getCoachId())
                .eq(CoachSchedule::getAvailableDate, dto.getAvailableDate())
                .eq(CoachSchedule::getStartTime, dto.getStartTime())
                .eq(CoachSchedule::getEndTime, dto.getEndTime())
        );
        if (existing > 0) {
            throw new BusinessException("该时间段已存在");
        }

        CoachSchedule schedule = new CoachSchedule();
        schedule.setCoachId(dto.getCoachId());
        schedule.setAvailableDate(dto.getAvailableDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setIsAvailable(true);
        schedule.setMaxSessions(dto.getMaxSessions() != null ? dto.getMaxSessions() : 4);
        schedule.setBookedSessions(0);

        coachScheduleMapper.insert(schedule);
        return schedule;
    }

    @Override
    @Transactional
    public List<CoachSchedule> batchCreateSchedule(CoachScheduleDTO dto, LocalDate endDate) {
        if (dto.getAvailableDate() == null || endDate == null) throw new BusinessException("日期范围不能为空");
        if (dto.getAvailableDate().isAfter(endDate)) throw new BusinessException("开始日期不能晚于结束日期");

        List<CoachSchedule> schedules = new ArrayList<>();
        LocalDate current = dto.getAvailableDate();
        while (!current.isAfter(endDate)) {
            dto.setAvailableDate(current);
            try {
                schedules.add(createSchedule(dto));
            } catch (BusinessException e) {
                // 跳过已存在的时间段
            }
            current = current.plusDays(1);
        }
        return schedules;
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        coachScheduleMapper.deleteById(id);
    }

    @Override
    public List<CoachSchedule> listByCoachAndDate(Long coachId, LocalDate date) {
        return coachScheduleMapper.selectList(
            Wrappers.<CoachSchedule>lambdaQuery()
                .eq(CoachSchedule::getCoachId, coachId)
                .eq(CoachSchedule::getAvailableDate, date)
                .orderByAsc(CoachSchedule::getStartTime)
        );
    }

    @Override
    public List<CoachSchedule> listByCoachAndDateRange(Long coachId, LocalDate startDate, LocalDate endDate) {
        return coachScheduleMapper.selectList(
            Wrappers.<CoachSchedule>lambdaQuery()
                .eq(CoachSchedule::getCoachId, coachId)
                .ge(CoachSchedule::getAvailableDate, startDate)
                .le(CoachSchedule::getAvailableDate, endDate)
                .eq(CoachSchedule::getIsAvailable, true)
                .orderByAsc(CoachSchedule::getAvailableDate)
                .orderByAsc(CoachSchedule::getStartTime)
        );
    }

    @Override
    public List<CoachSchedule> listByCoach(Long coachId) {
        return coachScheduleMapper.selectList(
            Wrappers.<CoachSchedule>lambdaQuery()
                .eq(CoachSchedule::getCoachId, coachId)
                .ge(CoachSchedule::getAvailableDate, LocalDate.now())
                .orderByAsc(CoachSchedule::getAvailableDate)
                .orderByAsc(CoachSchedule::getStartTime)
        );
    }

    @Override
    @Transactional
    public void setAvailability(Long id, Boolean available) {
        CoachSchedule schedule = coachScheduleMapper.selectById(id);
        if (schedule == null) throw new BusinessException("时间段不存在");
        schedule.setIsAvailable(available);
        coachScheduleMapper.updateById(schedule);
    }
}
