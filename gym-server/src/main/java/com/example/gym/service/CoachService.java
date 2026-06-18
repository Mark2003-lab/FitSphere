package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.entity.Coach;

import java.util.List;

public interface CoachService {
    Coach addCoach(Coach coach);
    Coach updateCoach(Long id, Coach coach);
    void deleteCoach(Long id);
    Coach getCoachById(Long id);
    Coach getCoachByPhone(String phone);
    IPage<Coach> listCoaches(int page, int size);
    List<Coach> listAllCoaches();
}
