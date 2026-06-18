package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.FitnessPlanDTO;
import com.example.gym.entity.FitnessPlan;
import com.example.gym.mapper.FitnessPlanMapper;
import com.example.gym.service.FitnessPlanService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FitnessPlanServiceImpl implements FitnessPlanService {
    
    private final FitnessPlanMapper fitnessPlanMapper;
    
    public FitnessPlanServiceImpl(FitnessPlanMapper fitnessPlanMapper) {
        this.fitnessPlanMapper = fitnessPlanMapper;
    }
    
    @Override
    public FitnessPlan createPlan(Long userId, FitnessPlanDTO dto) {
        FitnessPlan plan = new FitnessPlan();
        plan.setUserId(userId);
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setContent(dto.getContent());
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        
        fitnessPlanMapper.insert(plan);
        return plan;
    }
    
    @Override
    public FitnessPlan updatePlan(Long id, FitnessPlanDTO dto) {
        FitnessPlan plan = fitnessPlanMapper.selectById(id);
        if (plan != null) {
            plan.setPlanName(dto.getPlanName());
            plan.setDescription(dto.getDescription());
            plan.setContent(dto.getContent());
            plan.setUpdateTime(LocalDateTime.now());
            fitnessPlanMapper.updateById(plan);
        }
        return plan;
    }
    
    @Override
    public void deletePlan(Long id) {
        fitnessPlanMapper.deleteById(id);
    }
    
    @Override
    public FitnessPlan getPlanById(Long id) {
        return fitnessPlanMapper.selectById(id);
    }
    
    @Override
    public IPage<FitnessPlan> listPlans(Long userId, int page, int size) {
        return fitnessPlanMapper.selectPage(new Page<>(page, size),
                Wrappers.lambdaQuery(FitnessPlan.class).eq(FitnessPlan::getUserId, userId));
    }
}
