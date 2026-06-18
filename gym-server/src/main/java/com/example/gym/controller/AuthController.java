package com.example.gym.controller;

import com.example.gym.common.Response;
import com.example.gym.dto.LoginDTO;
import com.example.gym.dto.RegisterDTO;
import com.example.gym.dto.ResetPasswordDTO;
import com.example.gym.entity.User;
import com.example.gym.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        User user = userService.findByUsername(loginDTO.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("avatar", user.getAvatar());
        
        return Response.success("登录成功", result);
    }
    
    @PostMapping("/register")
    public Response<User> register(@RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return Response.success("注册成功", user);
    }
    
    @PostMapping("/send-code")
    public Response<String> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return Response.error("邮箱地址不能为空");
        }
        userService.sendVerificationCode(email);
        return Response.success("验证码已发送到您的邮箱");
    }
    
    @PostMapping("/send-reset-code")
    public Response<String> sendResetPasswordCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return Response.error("邮箱地址不能为空");
        }
        userService.sendResetPasswordCode(email);
        return Response.success("验证码已发送到您的邮箱");
    }
    
    @PostMapping("/reset-password")
    public Response<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return Response.success("密码重置成功,新密码已发送到您的邮箱");
    }
}
