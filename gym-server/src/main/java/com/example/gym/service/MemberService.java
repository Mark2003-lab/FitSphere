package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.dto.MemberDTO;
import com.example.gym.entity.Member;

public interface MemberService {
    Member addMember(MemberDTO memberDTO);
    Member addMemberEntity(Member member);
    Member updateMember(Long id, MemberDTO memberDTO);
    void deleteMember(Long id);
    Member getMemberById(Long id);
    Member getMemberByPhone(String phone);
    IPage<Member> listMembers(int page, int size);
}
