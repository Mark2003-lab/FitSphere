package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.dto.FitnessPlanDTO;
import com.example.gym.entity.FitnessPlan;
import com.example.gym.entity.User;
import com.example.gym.service.FitnessPlanService;
import com.example.gym.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
public class FitnessPlanController {
    
    private final FitnessPlanService fitnessPlanService;
    private final UserService userService;
    
    public FitnessPlanController(FitnessPlanService fitnessPlanService, UserService userService) {
        this.fitnessPlanService = fitnessPlanService;
        this.userService = userService;
    }
    
    @PostMapping
    public Response<FitnessPlan> createPlan(@RequestBody FitnessPlanDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Long userId = user.getId();
        
        FitnessPlan plan = fitnessPlanService.createPlan(userId, dto);
        return Response.success("保存成功", plan);
    }
    
    @PutMapping("/{id}")
    public Response<FitnessPlan> updatePlan(@PathVariable Long id, @RequestBody FitnessPlanDTO dto) {
        FitnessPlan plan = fitnessPlanService.updatePlan(id, dto);
        return Response.success("更新成功", plan);
    }
    
    @DeleteMapping("/{id}")
    public Response<Void> deletePlan(@PathVariable Long id) {
        fitnessPlanService.deletePlan(id);
        return Response.success("删除成功", null);
    }
    
    @GetMapping("/{id}")
    public Response<FitnessPlan> getPlanById(@PathVariable Long id) {
        FitnessPlan plan = fitnessPlanService.getPlanById(id);
        return Response.success(plan);
    }
    
    @GetMapping
    public Response<IPage<FitnessPlan>> listPlans(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Long userId = user.getId();
        
        IPage<FitnessPlan> plans = fitnessPlanService.listPlans(userId, page, size);
        return Response.success(plans);
    }
}
