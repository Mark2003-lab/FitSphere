package com.example.gym.service;

import com.example.gym.dto.CoachScheduleDTO;
import com.example.gym.entity.CoachSchedule;

import java.time.LocalDate;
import java.util.List;

public interface CoachScheduleService {

    /** 教练创建可用时间段 */
    CoachSchedule createSchedule(CoachScheduleDTO dto);

    /** 批量创建可用时间（按日期范围） */
    List<CoachSchedule> batchCreateSchedule(CoachScheduleDTO dto, LocalDate endDate);

    /** 删除时间段 */
    void deleteSchedule(Long id);

    /** 查询教练指定日期的可用时间 */
    List<CoachSchedule> listByCoachAndDate(Long coachId, LocalDate date);

    /** 查询教练指定日期范围的可用时间 */
    List<CoachSchedule> listByCoachAndDateRange(Long coachId, LocalDate startDate, LocalDate endDate);

    /** 查询教练所有可用时间 */
    List<CoachSchedule> listByCoach(Long coachId);

    /** 设置时间段是否可用 */
    void setAvailability(Long id, Boolean available);
}
