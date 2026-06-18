package com.example.gym.dto;

import com.example.gym.entity.Member;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员视图对象，包含关联的健身卡信息
 */
@Data
public class MemberVO {
    
    private Long id;
    private String name;
    private String gender;
    private String phone;
    private String level;
    private LocalDateTime joinDate;
    private LocalDateTime expireTime;
    
    /**
     * 从 Member 实体转换
     */
    public static MemberVO fromEntity(Member member) {
        MemberVO vo = new MemberVO();
        vo.setId(member.getId());
        vo.setName(member.getName());
        vo.setGender(member.getGender());
        vo.setPhone(member.getPhone());
        vo.setLevel(member.getLevel());
        vo.setJoinDate(member.getJoinDate());
        vo.setExpireTime(member.getExpireTime());
        return vo;
    }
    
    /**
     * 更新会员等级和到期时间（根据健身卡）
     */
    public void updateLevelAndExpireTime(String cardType, LocalDateTime cardExpireTime) {
        if (cardType != null && cardExpireTime != null) {
            switch (cardType) {
                case "MONTHLY":
                    this.level = "月卡会员";
                    break;
                case "QUARTERLY":
                    this.level = "季卡会员";
                    break;
                case "YEARLY":
                    this.level = "年卡会员";
                    break;
                default:
                    this.level = "普通会员";
            }
            this.expireTime = cardExpireTime;
        }
    }
}