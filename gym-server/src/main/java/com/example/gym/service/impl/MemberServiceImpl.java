package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.MemberDTO;
import com.example.gym.dto.MemberVO;
import com.example.gym.entity.Course;
import com.example.gym.entity.Member;
import com.example.gym.entity.Reservation;
import com.example.gym.entity.UserCard;
import com.example.gym.mapper.CourseMapper;
import com.example.gym.mapper.MemberMapper;
import com.example.gym.mapper.ReservationMapper;
import com.example.gym.mapper.UserCardMapper;
import com.example.gym.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {
    
    private final MemberMapper memberMapper;
    private final UserCardMapper userCardMapper;
    private final CourseMapper courseMapper;
    private final ReservationMapper reservationMapper;
    
    public MemberServiceImpl(MemberMapper memberMapper, UserCardMapper userCardMapper, 
                            CourseMapper courseMapper, ReservationMapper reservationMapper) {
        this.memberMapper = memberMapper;
        this.userCardMapper = userCardMapper;
        this.courseMapper = courseMapper;
        this.reservationMapper = reservationMapper;
    }
    
    @Override
    public Member addMember(MemberDTO memberDTO) {
        Member member = new Member();
        member.setName(memberDTO.getName());
        member.setGender(memberDTO.getGender());
        member.setPhone(memberDTO.getPhone());
        member.setLevel(memberDTO.getLevel());
        member.setExpireTime(memberDTO.getExpireTime());
        
        memberMapper.insert(member);
        return member;
    }
    
    @Override
    public Member addMemberEntity(Member member) {
        memberMapper.insert(member);
        return member;
    }
    
    @Override
    public Member updateMember(Long id, MemberDTO memberDTO) {
        Member member = memberMapper.selectById(id);
        if (member != null) {
            member.setName(memberDTO.getName());
            member.setGender(memberDTO.getGender());
            member.setPhone(memberDTO.getPhone());
            member.setLevel(memberDTO.getLevel());
            member.setExpireTime(memberDTO.getExpireTime());
            memberMapper.updateById(member);
        }
        return member;
    }
    
    @Override
    public void deleteMember(Long id) {
        memberMapper.deleteById(id);
    }
    
    @Override
    public Member getMemberById(Long id) {
        return memberMapper.selectById(id);
    }
    
    @Override
    public Member getMemberByPhone(String phone) {
        if (phone == null || phone.isEmpty()) return null;
        return memberMapper.selectOne(Wrappers.<Member>lambdaQuery()
                .eq(Member::getPhone, phone)
                .last("LIMIT 1"));
    }
    
    @Override
    public IPage<Member> listMembers(int page, int size) {
        return memberMapper.selectPage(new Page<>(page, size), Wrappers.emptyWrapper());
    }
    
    /**
     * 获取会员列表（包含健身卡信息）
     */
    public IPage<MemberVO> listMembersWithCard(int page, int size) {
        IPage<Member> memberPage = memberMapper.selectPage(new Page<>(page, size), Wrappers.emptyWrapper());
        
        // 获取所有会员ID
        List<Long> memberIds = memberPage.getRecords().stream()
                .map(Member::getId)
                .collect(Collectors.toList());
        
        // 查询这些会员的当前生效健身卡
        Map<Long, UserCard> userCardMap = memberIds.isEmpty() ? Map.of() : 
            userCardMapper.selectList(Wrappers.<UserCard>lambdaQuery()
                    .in(UserCard::getUserId, memberIds)
                    .eq(UserCard::getStatus, 1))
                .stream()
                .collect(Collectors.toMap(UserCard::getUserId, card -> card));
        
        // 转换为 MemberVO
        List<MemberVO> voList = memberPage.getRecords().stream()
                .map(member -> {
                    MemberVO vo = MemberVO.fromEntity(member);
                    UserCard card = userCardMap.get(member.getId());
                    if (card != null) {
                        vo.updateLevelAndExpireTime(card.getCardType(), card.getExpireTime());
                    } else {
                        vo.setLevel("普通会员");
                        vo.setExpireTime(null);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        
        IPage<MemberVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(memberPage.getTotal());
        voPage.setCurrent(memberPage.getCurrent());
        voPage.setSize(memberPage.getSize());
        
        return voPage;
    }
    
    /**
     * 获取教练的会员列表（预约了该教练课程的会员）
     */
    public IPage<MemberVO> listMembersByCoach(Long coachId, int page, int size) {
        // 获取教练的所有课程
        List<Course> courses = courseMapper.selectList(Wrappers.<Course>lambdaQuery()
                .eq(Course::getCoachId, coachId));
        
        if (courses.isEmpty()) {
            IPage<MemberVO> voPage = new Page<>();
            voPage.setRecords(List.of());
            voPage.setTotal(0);
            return voPage;
        }
        
        // 获取课程ID列表
        List<Long> courseIds = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        
        // 获取这些课程的已通过预约记录
        List<Reservation> reservations = reservationMapper.selectList(Wrappers.<Reservation>lambdaQuery()
                .in(Reservation::getCourseId, courseIds)
                .eq(Reservation::getStatus, "APPROVED"));
        
        // 获取会员ID列表（去重）
        Set<Long> memberIds = reservations.stream()
                .map(Reservation::getMemberId)
                .collect(Collectors.toSet());
        
        if (memberIds.isEmpty()) {
            IPage<MemberVO> voPage = new Page<>();
            voPage.setRecords(List.of());
            voPage.setTotal(0);
            return voPage;
        }
        
        // 查询会员信息（分页）
        IPage<Member> memberPage = memberMapper.selectPage(new Page<>(page, size), 
            Wrappers.<Member>lambdaQuery().in(Member::getId, memberIds));
        
        // 查询这些会员的当前生效健身卡
        Map<Long, UserCard> userCardMap = userCardMapper.selectList(Wrappers.<UserCard>lambdaQuery()
                .in(UserCard::getUserId, memberIds)
                .eq(UserCard::getStatus, 1))
            .stream()
            .collect(Collectors.toMap(UserCard::getUserId, card -> card));
        
        // 转换为 MemberVO
        List<MemberVO> voList = memberPage.getRecords().stream()
                .map(member -> {
                    MemberVO vo = MemberVO.fromEntity(member);
                    UserCard card = userCardMap.get(member.getId());
                    if (card != null) {
                        vo.updateLevelAndExpireTime(card.getCardType(), card.getExpireTime());
                    } else {
                        vo.setLevel("普通会员");
                        vo.setExpireTime(null);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        
        IPage<MemberVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(memberPage.getTotal());
        voPage.setCurrent(memberPage.getCurrent());
        voPage.setSize(memberPage.getSize());
        
        return voPage;
    }
}
