package com.example.gym.dto;

import com.example.gym.entity.Course;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程视图对象，包含教练名称
 */
@Data
public class CourseVO {
    
    private Long id;
    private String courseName;
    private Long coachId;
    private String coachName;
    private LocalDateTime courseTime;
    private Integer capacity;
    private String location;
    private java.math.BigDecimal price;
    private String status;
    
    /** 审核状态 */
    private String auditStatus;
    
    private Integer currentReservations;
    private Integer availableSlots;
    
    /** 是否开启签到 */
    private Boolean checkinEnabled;
    
    /** 签到开始时间 */
    private LocalDateTime checkinStartTime;
    
    /** 签到结束时间 */
    private LocalDateTime checkinEndTime;
    
    /**
     * 从 Course 实体转换
     */
    public static CourseVO fromEntity(Course course) {
        CourseVO vo = new CourseVO();
        vo.setId(course.getId());
        vo.setCourseName(course.getCourseName());
        vo.setCoachId(course.getCoachId());
        vo.setCourseTime(course.getCourseTime());
        vo.setCapacity(course.getCapacity());
        vo.setLocation(course.getLocation());
        vo.setPrice(course.getPrice());
        vo.setStatus(course.getStatus());
        vo.setAuditStatus(course.getAuditStatus());
        vo.setCheckinEnabled(course.getCheckinEnabled());
        vo.setCheckinStartTime(course.getCheckinStartTime());
        vo.setCheckinEndTime(course.getCheckinEndTime());
        
        // 设置当前预约人数和剩余名额
        Integer currentReservations = course.getCurrentReservations() != null ? course.getCurrentReservations() : 0;
        vo.setCurrentReservations(currentReservations);
        
        Integer capacity = course.getCapacity() != null ? course.getCapacity() : 0;
        vo.setAvailableSlots(capacity - currentReservations);
        
        return vo;
    }
}