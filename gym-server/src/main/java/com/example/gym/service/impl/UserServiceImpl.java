package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.gym.dto.LoginDTO;
import org.springframework.cache.annotation.CacheEvict;
import com.example.gym.dto.RegisterDTO;
import com.example.gym.dto.ResetPasswordDTO;
import com.example.gym.entity.Coach;
import com.example.gym.entity.Member;
import com.example.gym.entity.User;
import com.example.gym.exception.BusinessException;
import com.example.gym.mapper.CoachMapper;
import com.example.gym.mapper.MemberMapper;
import com.example.gym.mapper.UserMapper;
import com.example.gym.security.JwtTokenUtil;
import com.example.gym.service.CoachService;
import com.example.gym.service.EmailService;
import com.example.gym.service.LoginSecurityService;
import com.example.gym.service.MemberService;
import com.example.gym.service.UserService;
import com.example.gym.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final MemberService memberService;
    private final CoachService coachService;
    private final MemberMapper memberMapper;
    private final CoachMapper coachMapper;
    private final LoginSecurityService loginSecurityService;
    
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, 
                          JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager,
                          EmailService emailService, VerificationCodeService verificationCodeService,
                          MemberService memberService, CoachService coachService,
                          MemberMapper memberMapper, CoachMapper coachMapper,
                          LoginSecurityService loginSecurityService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
        this.memberService = memberService;
        this.coachService = coachService;
        this.memberMapper = memberMapper;
        this.coachMapper = coachMapper;
        this.loginSecurityService = loginSecurityService;
    }
    
    @Override
    public User register(RegisterDTO registerDTO) {
        // 1. 验证用户名是否已存在
        User existingUser = userMapper.findByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 2. 验证邮箱是否已被正式用户注册
        QueryWrapper<User> emailQuery = new QueryWrapper<>();
        emailQuery.eq("email", registerDTO.getEmail());
        User existingUserWithEmail = userMapper.selectOne(emailQuery);
        if (existingUserWithEmail != null) {
            throw new RuntimeException("该邮箱已被注册");
        }
        
        // 3. 验证验证码(从内存中验证)
        if (!verificationCodeService.verifyCode(registerDTO.getEmail(), 
                VerificationCodeService.CodeType.REGISTER, registerDTO.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 4. 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole() != null ? registerDTO.getRole() : "MEMBER");
        user.setEmail(registerDTO.getEmail());
        user.setCreateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 5. 根据角色自动创建对应的 member 或 coach 记录
        String role = user.getRole();
        if ("MEMBER".equals(role)) {
            Member member = new Member();
            member.setId(user.getId());
            member.setName(user.getUsername());
            member.setPhone(user.getPhone());
            member.setGender(user.getGender());
            member.setLevel("NORMAL");
            memberService.addMemberEntity(member);
        } else if ("COACH".equals(role)) {
            Coach coach = new Coach();
            coach.setId(user.getId());
            coach.setName(user.getUsername());
            coach.setPhone(user.getPhone());
            coach.setSpeciality("");
            coachService.addCoach(coach);
        }
        
        return user;
    }
    
    @Override
    public String login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        
        // 1. 检查账号是否被锁定
        if (loginSecurityService.isLocked(username)) {
            long remainingSeconds = loginSecurityService.getRemainingLockSeconds(username);
            int remainingMinutes = (int) Math.ceil(remainingSeconds / 60.0);
            throw new BusinessException("账号已被锁定，请 " + remainingMinutes + " 分钟后再试");
        }
        
        try {
            // 2. 验证用户名密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginDTO.getPassword())
            );
            
            // 3. 登录成功，清除错误记录
            loginSecurityService.clearError(username);
            
            // 4. 生成Token
            User user = userMapper.findByUsername(username);
            return jwtTokenUtil.generateToken(user.getUsername(), user.getRole());
            
        } catch (BadCredentialsException e) {
            // 5. 密码错误，记录错误次数
            int remainingAttempts = loginSecurityService.recordError(username);
            if (remainingAttempts == 0) {
                throw new BusinessException("密码错误次数过多，账号已被锁定 5 分钟");
            } else {
                throw new BusinessException("用户名或密码错误，剩余尝试次数: " + remainingAttempts);
            }
        }
    }
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public void sendVerificationCode(String email) {
        // 1. 检查邮箱是否已被注册
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", email);
        User existingUser = userMapper.selectOne(query);
        
        if (existingUser != null) {
            throw new RuntimeException("该邮箱已被注册");
        }
        
        // 2. 检查是否已经有未过期的验证码
        if (verificationCodeService.hasCode(email, VerificationCodeService.CodeType.REGISTER)) {
            throw new RuntimeException("验证码已发送,请稍后再试");
        }
        
        // 3. 生成验证码(存储在内存中)
        String code = verificationCodeService.generateCode(email, VerificationCodeService.CodeType.REGISTER);
        
        // 4. 异步发送邮件
        new Thread(() -> {
            try {
                emailService.sendVerificationCode(email, code);
            } catch (Exception e) {
                log.error("异步发送邮件失败: {}", email, e);
                // 发送失败时清除验证码
                verificationCodeService.verifyCode(email, VerificationCodeService.CodeType.REGISTER, code);
            }
        }).start();
    }
    
    @Override
    public void sendResetPasswordCode(String email) {
        // 1. 检查邮箱是否已注册
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", email);
        User user = userMapper.selectOne(query);
        
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }
        
        // 2. 检查是否已经有未过期的验证码
        if (verificationCodeService.hasCode(email, VerificationCodeService.CodeType.RESET_PASSWORD)) {
            throw new RuntimeException("验证码已发送,请稍后再试");
        }
        
        // 3. 生成验证码(存储在内存中)
        String code = verificationCodeService.generateCode(email, VerificationCodeService.CodeType.RESET_PASSWORD);
        
        // 4. 异步发送邮件
        new Thread(() -> {
            try {
                emailService.sendVerificationCode(email, code);
            } catch (Exception e) {
                log.error("异步发送重置密码邮件失败: {}", email, e);
                verificationCodeService.verifyCode(email, VerificationCodeService.CodeType.RESET_PASSWORD, code);
            }
        }).start();
    }
    
    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 1. 验证验证码
        if (!verificationCodeService.verifyCode(resetPasswordDTO.getEmail(), 
                VerificationCodeService.CodeType.RESET_PASSWORD, resetPasswordDTO.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 2. 查询用户
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", resetPasswordDTO.getEmail());
        User user = userMapper.selectOne(query);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 3. 重置密码为123456
        String newPassword = "123456";
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        
        // 4. 异步发送密码重置成功邮件
        String finalNewPassword = newPassword;
        new Thread(() -> {
            try {
                emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), finalNewPassword);
            } catch (Exception e) {
                log.error("发送密码重置成功邮件失败: {}", user.getEmail(), e);
            }
        }).start();
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    @CacheEvict(value = {"coach:list", "coach:detail"}, allEntries = true)
    public User update(User user) {
        userMapper.updateById(user);
        
        // 同步更新 member 或 coach 表
        String role = user.getRole();
        if ("MEMBER".equals(role)) {
            Member member = memberMapper.selectById(user.getId());
            if (member != null) {
                member.setName(user.getUsername());
                member.setPhone(user.getPhone());
                member.setGender(user.getGender());
                member.setAvatar(user.getAvatar());
                memberMapper.updateById(member);
            }
        } else if ("COACH".equals(role)) {
            Coach coach = coachMapper.selectById(user.getId());
            if (coach != null) {
                coach.setName(user.getUsername());
                coach.setPhone(user.getPhone());
                coach.setAvatar(user.getAvatar());
                coachMapper.updateById(coach);
            }
        }
        
        return user;
    }
    
    @Override
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return true;
    }
    
    @Override
    public boolean unlockAccount(String username) {
        // 检查用户是否存在
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return loginSecurityService.unlock(username);
    }
    
    @Override
    public LoginSecurityInfo getLoginSecurityInfo(String username) {
        boolean locked = loginSecurityService.isLocked(username);
        int remainingAttempts = loginSecurityService.getRemainingAttempts(username);
        Long remainingLockSeconds = locked ? loginSecurityService.getRemainingLockSeconds(username) : null;
        return new LoginSecurityInfo(locked, remainingAttempts, remainingLockSeconds);
    }
}
