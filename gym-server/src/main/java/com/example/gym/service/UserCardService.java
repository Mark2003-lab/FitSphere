package com.example.gym.service;

import com.example.gym.entity.UserCard;

import java.util.List;

/**
 * 用户购卡记录服务接口
 */
public interface UserCardService {

    /**
     * 用户购买健身卡
     */
    UserCard purchaseCard(Long userId, Long cardId);

    /**
     * 根据用户ID查询当前生效的卡
     */
    UserCard getCurrentCard(Long userId);

    /**
     * 根据用户ID查询所有购卡记录
     */
    List<UserCard> getUserCards(Long userId);

    /**
     * 根据ID查询购卡记录
     */
    UserCard getCardById(Long id);

    /**
     * 更新卡状态（过期处理）
     */
    void updateCardStatus();

    /**
     * 换卡：用户将当前卡更换为其他类型的卡
     * @param userId 用户ID
     * @param newCardId 新卡ID
     * @return 新的购卡记录
     */
    UserCard changeCard(Long userId, Long newCardId);

    /**
     * 续卡：用户为当前卡续期
     * @param userId 用户ID
     * @param cardId 要续期的卡ID
     * @return 更新后的购卡记录
     */
    UserCard renewCard(Long userId, Long cardId);
}
