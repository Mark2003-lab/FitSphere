package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("coach_schedule")
public class CoachSchedule {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("coach_id")
    private Long coachId;

    @TableField("available_date")
    private LocalDate availableDate;

    @TableField("start_time")
    private LocalTime startTime;

    @TableField("end_time")
    private LocalTime endTime;

    @TableField("is_available")
    private Boolean isAvailable;

    @TableField("max_sessions")
    private Integer maxSessions;

    @TableField("booked_sessions")
    private Integer bookedSessions;
}
