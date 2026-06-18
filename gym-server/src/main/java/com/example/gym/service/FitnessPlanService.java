package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.dto.FitnessPlanDTO;
import com.example.gym.entity.FitnessPlan;

public interface FitnessPlanService {
    
    FitnessPlan createPlan(Long userId, FitnessPlanDTO dto);
    
    FitnessPlan updatePlan(Long id, FitnessPlanDTO dto);
    
    void deletePlan(Long id);
    
    FitnessPlan getPlanById(Long id);
    
    IPage<FitnessPlan> listPlans(Long userId, int page, int size);
}
