package com.example.gym.controller;

import com.example.gym.common.Response;
import com.example.gym.entity.Coach;
import com.example.gym.entity.Member;
import com.example.gym.entity.User;
import com.example.gym.mapper.CoachMapper;
import com.example.gym.mapper.MemberMapper;
import com.example.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private CoachMapper coachMapper;
    
    @Autowired
    private MemberMapper memberMapper;
    
    @GetMapping("/{id}")
    public Response<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Response.success(user);
    }
    
    @PutMapping("/{id}")
    public Response<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        User user = userService.findById(id);
        if (user == null) {
            return Response.error("用户不存在");
        }
        
        String newAvatar = null;
        if (updateData.containsKey("email")) {
            user.setEmail((String) updateData.get("email"));
        }
        if (updateData.containsKey("phone")) {
            user.setPhone((String) updateData.get("phone"));
        }
        if (updateData.containsKey("gender")) {
            user.setGender((String) updateData.get("gender"));
        }
        if (updateData.containsKey("avatar")) {
            newAvatar = (String) updateData.get("avatar");
            user.setAvatar(newAvatar);
        }
        
        User updatedUser = userService.update(user);
        updatedUser.setPassword(null);

        // 如果该用户是教练，同步更新 coach 表的头像
        if (newAvatar != null && "COACH".equals(user.getRole())) {
            try {
                Coach coach = coachMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Coach>()
                        .eq(Coach::getUserId, id)
                );
                if (coach != null) {
                    coach.setAvatar(newAvatar);
                    coachMapper.updateById(coach);
                }
            } catch (Exception ignored) {
                // 头像同步到 coach 表失败不影响主流程
            }
        }

        // 如果该用户是会员，同步更新 member 表的头像（用于排行榜显示）
        if (newAvatar != null && "MEMBER".equals(user.getRole())) {
            try {
                Member member = memberMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Member>()
                        .eq(Member::getPhone, user.getPhone())
                );
                if (member != null) {
                    member.setAvatar(newAvatar);
                    memberMapper.updateById(member);
                }
            } catch (Exception ignored) {
                // 头像同步到 member 表失败不影响主流程
            }
        }

        return Response.success("更新成功", updatedUser);
    }
    
    @PostMapping("/{id}/change-password")
    public Response<String> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        if (oldPassword == null || oldPassword.isEmpty()) {
            return Response.error("请输入原密码");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return Response.error("请输入新密码");
        }
        if (newPassword.length() < 6) {
            return Response.error("新密码长度不能少于6位");
        }
        
        boolean success = userService.changePassword(id, oldPassword, newPassword);
        if (success) {
            return Response.success("密码修改成功");
        } else {
            return Response.error("原密码错误");
        }
    }
    
    /**
     * 解锁账号（管理员使用）
     */
    @PostMapping("/unlock/{username}")
    public Response<String> unlockAccount(@PathVariable String username) {
        try {
            boolean success = userService.unlockAccount(username);
            if (success) {
                return Response.success("账号解锁成功");
            } else {
                return Response.error("账号解锁失败");
            }
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
    
    /**
     * 获取账号登录安全信息
     */
    @GetMapping("/security/{username}")
    public Response<UserService.LoginSecurityInfo> getLoginSecurityInfo(@PathVariable String username) {
        UserService.LoginSecurityInfo info = userService.getLoginSecurityInfo(username);
        return Response.success(info);
    }
}
