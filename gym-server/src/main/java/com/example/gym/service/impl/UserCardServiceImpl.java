package com.example.gym.service.impl;

import com.example.gym.entity.FitnessCard;
import com.example.gym.entity.Member;
import com.example.gym.entity.UserCard;
import com.example.gym.exception.BusinessException;
import com.example.gym.mapper.FitnessCardMapper;
import com.example.gym.mapper.MemberMapper;
import com.example.gym.mapper.UserCardMapper;
import com.example.gym.service.PrivateCoachingCouponService;
import com.example.gym.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户购卡记录服务实现类
 */
@Service
public class UserCardServiceImpl implements UserCardService {

    @Autowired
    private UserCardMapper userCardMapper;

    @Autowired
    private FitnessCardMapper fitnessCardMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PrivateCoachingCouponService couponService;

    @Override
    @Transactional
    public UserCard purchaseCard(Long userId, Long cardId) {
        FitnessCard fitnessCard = fitnessCardMapper.selectById(cardId);
        if (fitnessCard == null) {
            throw new BusinessException("健身卡不存在");
        }
        if (fitnessCard.getStatus() != 1) {
            throw new BusinessException("该健身卡已下架");
        }

        // 查询用户当前是否有生效中的卡
        UserCard currentCard = userCardMapper.selectCurrentCardByUserId(userId);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now;
        LocalDateTime expireTime;
        
        // 如果当前有生效中的卡，新卡到期时间在原卡基础上顺延
        if (currentCard != null && currentCard.getExpireTime().isAfter(now)) {
            expireTime = currentCard.getExpireTime().plusDays(fitnessCard.getValidDays());
        } else {
            expireTime = now.plusDays(fitnessCard.getValidDays());
        }

        UserCard userCard = new UserCard();
        userCard.setUserId(userId);
        userCard.setCardId(cardId);
        userCard.setCardName(fitnessCard.getCardName());
        userCard.setCardType(fitnessCard.getCardType());
        userCard.setPurchaseTime(now);
        userCard.setStartTime(startTime);
        userCard.setExpireTime(expireTime);
        userCard.setStatus(1);
        userCard.setCreateTime(now);
        
        // 首充赠送私教兑换券（必须在插入购卡记录前判断）
        boolean isFirst = couponService.isFirstPurchase(userId);
        int giftCount = fitnessCard.getGiftCouponCount() != null ? fitnessCard.getGiftCouponCount() : 0;
        
        userCardMapper.insert(userCard);
        
        // 更新之前生效卡的状态
        if (currentCard != null) {
            currentCard.setStatus(0);
            userCardMapper.updateById(currentCard);
        }
        
        // 更新 member 表的等级和到期时间
        Member member = memberMapper.selectById(userId);
        if (member != null) {
            // 根据卡类型设置会员等级
            String cardType = fitnessCard.getCardType();
            switch (cardType) {
                case "MONTHLY":
                    member.setLevel("月卡会员");
                    break;
                case "QUARTERLY":
                    member.setLevel("季卡会员");
                    break;
                case "YEARLY":
                    member.setLevel("年卡会员");
                    break;
                default:
                    member.setLevel("普通会员");
            }
            member.setExpireTime(expireTime);
            memberMapper.updateById(member);
        }
        
        // 发放首充兑换券
        if (isFirst && giftCount > 0) {
            couponService.grantCoupons(userId, giftCount);
        }
        
        return userCard;
    }

    @Override
    public UserCard getCurrentCard(Long userId) {
        return userCardMapper.selectCurrentCardByUserId(userId);
    }

    @Override
    public List<UserCard> getUserCards(Long userId) {
        return userCardMapper.selectByUserId(userId);
    }

    @Override
    public UserCard getCardById(Long id) {
        return userCardMapper.selectById(id);
    }

    @Override
    @Transactional
    public void updateCardStatus() {
        // 查询所有过期但状态仍为生效中的卡
        List<UserCard> expiredCards = userCardMapper.selectList(null);
        LocalDateTime now = LocalDateTime.now();
        for (UserCard card : expiredCards) {
            if (card.getStatus() == 1 && card.getExpireTime().isBefore(now)) {
                card.setStatus(0);
                userCardMapper.updateById(card);
            }
        }
    }

    @Override
    @Transactional
    public UserCard changeCard(Long userId, Long newCardId) {
        FitnessCard newFitnessCard = fitnessCardMapper.selectById(newCardId);
        if (newFitnessCard == null) {
            throw new BusinessException("新的健身卡不存在");
        }
        if (newFitnessCard.getStatus() != 1) {
            throw new BusinessException("该健身卡已下架");
        }

        // 查询用户当前生效中的卡
        UserCard currentCard = userCardMapper.selectCurrentCardByUserId(userId);
        if (currentCard == null) {
            throw new BusinessException("当前没有生效中的卡，无法换卡");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // 计算剩余天数比例，用于折算
        long remainingDays = java.time.Duration.between(now, currentCard.getExpireTime()).toDays();
        long originalDays = java.time.Duration.between(currentCard.getStartTime(), currentCard.getExpireTime()).toDays();
        
        // 新卡到期时间 = 当前时间 + 新卡天数 * (剩余天数/原卡天数) + 新卡天数
        long newValidDays = newFitnessCard.getValidDays();
        long addedDays = (long) (newValidDays * (remainingDays * 1.0 / originalDays)) + newValidDays;
        LocalDateTime expireTime = now.plusDays(addedDays);

        // 创建新的购卡记录
        UserCard userCard = new UserCard();
        userCard.setUserId(userId);
        userCard.setCardId(newCardId);
        userCard.setCardName(newFitnessCard.getCardName());
        userCard.setCardType(newFitnessCard.getCardType());
        userCard.setPurchaseTime(now);
        userCard.setStartTime(now);
        userCard.setExpireTime(expireTime);
        userCard.setStatus(1);
        userCard.setCreateTime(now);
        
        userCardMapper.insert(userCard);
        
        // 更新原卡状态为已过期
        currentCard.setStatus(0);
        userCardMapper.updateById(currentCard);
        
        // 更新会员等级和到期时间
        Member member = memberMapper.selectById(userId);
        if (member != null) {
            String cardType = newFitnessCard.getCardType();
            switch (cardType) {
                case "MONTHLY":
                    member.setLevel("月卡会员");
                    break;
                case "QUARTERLY":
                    member.setLevel("季卡会员");
                    break;
                case "YEARLY":
                    member.setLevel("年卡会员");
                    break;
                default:
                    member.setLevel("普通会员");
            }
            member.setExpireTime(expireTime);
            memberMapper.updateById(member);
        }
        
        return userCard;
    }

    @Override
    @Transactional
    public UserCard renewCard(Long userId, Long cardId) {
        FitnessCard fitnessCard = fitnessCardMapper.selectById(cardId);
        if (fitnessCard == null) {
            throw new BusinessException("健身卡不存在");
        }
        if (fitnessCard.getStatus() != 1) {
            throw new BusinessException("该健身卡已下架");
        }

        // 查询用户当前生效中的卡
        UserCard currentCard = userCardMapper.selectCurrentCardByUserId(userId);
        if (currentCard == null) {
            throw new BusinessException("当前没有生效中的卡");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime;
        
        // 如果当前卡还未过期，在原到期时间基础上顺延；否则从现在开始计算
        if (currentCard.getExpireTime().isAfter(now)) {
            expireTime = currentCard.getExpireTime().plusDays(fitnessCard.getValidDays());
        } else {
            expireTime = now.plusDays(fitnessCard.getValidDays());
        }

        // 创建新的购卡记录
        UserCard userCard = new UserCard();
        userCard.setUserId(userId);
        userCard.setCardId(cardId);
        userCard.setCardName(fitnessCard.getCardName());
        userCard.setCardType(fitnessCard.getCardType());
        userCard.setPurchaseTime(now);
        userCard.setStartTime(now);
        userCard.setExpireTime(expireTime);
        userCard.setStatus(1);
        userCard.setCreateTime(now);
        
        userCardMapper.insert(userCard);
        
        // 更新原卡状态为已过期
        currentCard.setStatus(0);
        userCardMapper.updateById(currentCard);
        
        // 更新会员等级和到期时间
        Member member = memberMapper.selectById(userId);
        if (member != null) {
            String cardType = fitnessCard.getCardType();
            switch (cardType) {
                case "MONTHLY":
                    member.setLevel("月卡会员");
                    break;
                case "QUARTERLY":
                    member.setLevel("季卡会员");
                    break;
                case "YEARLY":
                    member.setLevel("年卡会员");
                    break;
                default:
                    member.setLevel("普通会员");
            }
            member.setExpireTime(expireTime);
            memberMapper.updateById(member);
        }
        
        return userCard;
    }
}
