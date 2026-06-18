package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.FitnessCardDTO;
import com.example.gym.entity.FitnessCard;

import java.util.List;

/**
 * 健身卡服务接口
 */
public interface FitnessCardService {

    /**
     * 创建健身卡
     */
    FitnessCard createCard(FitnessCardDTO dto);

    /**
     * 更新健身卡
     */
    FitnessCard updateCard(Long id, FitnessCardDTO dto);

    /**
     * 删除健身卡
     */
    void deleteCard(Long id);

    /**
     * 根据ID查询健身卡
     */
    FitnessCard getCardById(Long id);

    /**
     * 分页查询所有健身卡
     */
    IPage<FitnessCard> listCards(Page<FitnessCard> page);

    /**
     * 查询所有上架的健身卡
     */
    List<FitnessCard> listActiveCards();

    /**
     * 上下架切换
     */
    FitnessCard toggleStatus(Long id);
}
