package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.dto.CourseDTO;
import com.example.gym.entity.Course;

public interface CourseService {
    Course addCourse(CourseDTO courseDTO);
    
    /**
     * 教练创建课程（需要审核）
     */
    Course coachCreateCourse(CourseDTO courseDTO, Long coachId);
    
    /**
     * 管理员审核课程
     */
    Course auditCourse(Long courseId, String auditStatus, Long auditorId);
    
    /**
     * 获取待审核课程列表
     */
    IPage<Course> listPendingCourses(int page, int size);
    
    Course updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);
    Course getCourseById(Long id);
    IPage<Course> listCourses(int page, int size);
}