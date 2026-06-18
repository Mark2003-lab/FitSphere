package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.entity.Coach;
import com.example.gym.entity.User;
import com.example.gym.service.CoachService;
import com.example.gym.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach")
public class CoachController {
    
    private final CoachService coachService;
    private final UserService userService;
    
    public CoachController(CoachService coachService, UserService userService) {
        this.coachService = coachService;
        this.userService = userService;
    }
    
    @PostMapping
    public Response<Coach> addCoach(@RequestBody Coach coach) {
        Coach result = coachService.addCoach(coach);
        return Response.success("添加成功", result);
    }
    
    @PutMapping("/{id}")
    public Response<Coach> updateCoach(@PathVariable Long id, @RequestBody Coach coach) {
        Coach result = coachService.updateCoach(id, coach);
        return Response.success("更新成功", result);
    }
    
    @DeleteMapping("/{id}")
    public Response<Void> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return Response.success("删除成功", null);
    }
    
    @GetMapping("/{id}")
    public Response<Coach> getCoachById(@PathVariable Long id) {
        Coach coach = coachService.getCoachById(id);
        return Response.success(coach);
    }
    
    @GetMapping
    public Response<IPage<Coach>> listCoaches(@RequestParam(defaultValue = "1") int page, 
                                              @RequestParam(defaultValue = "10") int size) {
        IPage<Coach> coaches = coachService.listCoaches(page, size);
        return Response.success(coaches);
    }
    
    @GetMapping("/me")
    public Response<Coach> getCurrentCoach() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.error("用户不存在");
        }
        Coach coach = coachService.getCoachById(user.getId());
        if (coach == null) {
            return Response.error("教练信息不存在");
        }
        return Response.success(coach);
    }

    /** 获取所有教练列表（不分页，会员浏览用） */
    @GetMapping("/all")
    public Response<List<Coach>> listAllCoaches() {
        List<Coach> coaches = coachService.listAllCoaches();
        return Response.success(coaches);
    }
}
