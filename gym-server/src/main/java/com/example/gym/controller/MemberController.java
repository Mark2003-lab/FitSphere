package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.dto.MemberDTO;
import com.example.gym.dto.MemberVO;
import com.example.gym.entity.Member;
import com.example.gym.entity.User;
import com.example.gym.service.MemberService;
import com.example.gym.service.UserService;
import com.example.gym.service.impl.MemberServiceImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    
    private final MemberService memberService;
    private final MemberServiceImpl memberServiceImpl;
    private final UserService userService;
    
    public MemberController(MemberService memberService, MemberServiceImpl memberServiceImpl, UserService userService) {
        this.memberService = memberService;
        this.memberServiceImpl = memberServiceImpl;
        this.userService = userService;
    }
    
    @PostMapping
    public Response<Member> addMember(@RequestBody MemberDTO memberDTO) {
        Member member = memberService.addMember(memberDTO);
        return Response.success("添加成功", member);
    }
    
    @PutMapping("/{id}")
    public Response<Member> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        Member member = memberService.updateMember(id, memberDTO);
        return Response.success("更新成功", member);
    }
    
    @DeleteMapping("/{id}")
    public Response<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return Response.success("删除成功", null);
    }
    
    @GetMapping("/{id}")
    public Response<Member> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return Response.success(member);
    }
    
    @GetMapping
    public Response<IPage<MemberVO>> listMembers(@RequestParam(defaultValue = "1") int page, 
                                                 @RequestParam(defaultValue = "10") int size) {
        IPage<MemberVO> members = memberServiceImpl.listMembersWithCard(page, size);
        return Response.success(members);
    }
    
    @GetMapping("/coach")
    public Response<IPage<MemberVO>> listCoachMembers(@RequestParam(defaultValue = "1") int page, 
                                                      @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.error("用户不存在");
        }
        IPage<MemberVO> members = memberServiceImpl.listMembersByCoach(user.getId(), page, size);
        return Response.success(members);
    }
}
