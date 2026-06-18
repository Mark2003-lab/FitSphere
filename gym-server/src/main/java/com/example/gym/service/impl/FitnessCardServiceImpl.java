package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.FitnessCardDTO;
import com.example.gym.entity.FitnessCard;
import com.example.gym.exception.BusinessException;
import com.example.gym.mapper.FitnessCardMapper;
import com.example.gym.service.FitnessCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健身卡服务实现类
 */
@Service
public class FitnessCardServiceImpl implements FitnessCardService {

    @Autowired
    private FitnessCardMapper fitnessCardMapper;

    @Override
    @Transactional
    public FitnessCard createCard(FitnessCardDTO dto) {
        FitnessCard card = new FitnessCard();
        card.setCardName(dto.getCardName());
        card.setCardType(dto.getCardType());
        card.setPrice(dto.getPrice());
        card.setValidDays(dto.getValidDays());
        card.setDescription(dto.getDescription());
        card.setGiftCouponCount(dto.getGiftCouponCount() != null ? dto.getGiftCouponCount() : 0);
        card.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        card.setCreateTime(LocalDateTime.now());
        card.setUpdateTime(LocalDateTime.now());
        fitnessCardMapper.insert(card);
        return card;
    }

    @Override
    @Transactional
    public FitnessCard updateCard(Long id, FitnessCardDTO dto) {
        FitnessCard card = fitnessCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException("健身卡不存在");
        }
        card.setCardName(dto.getCardName());
        card.setCardType(dto.getCardType());
        card.setPrice(dto.getPrice());
        card.setValidDays(dto.getValidDays());
        card.setDescription(dto.getDescription());
        if (dto.getGiftCouponCount() != null) {
            card.setGiftCouponCount(dto.getGiftCouponCount());
        }
        if (dto.getStatus() != null) {
            card.setStatus(dto.getStatus());
        }
        card.setUpdateTime(LocalDateTime.now());
        fitnessCardMapper.updateById(card);
        return card;
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        FitnessCard card = fitnessCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException("健身卡不存在");
        }
        fitnessCardMapper.deleteById(id);
    }

    @Override
    public FitnessCard getCardById(Long id) {
        return fitnessCardMapper.selectById(id);
    }

    @Override
    public IPage<FitnessCard> listCards(Page<FitnessCard> page) {
        return fitnessCardMapper.selectPage(page, null);
    }

    @Override
    public List<FitnessCard> listActiveCards() {
        return fitnessCardMapper.selectList(null);
    }

    @Override
    @Transactional
    public FitnessCard toggleStatus(Long id) {
        FitnessCard card = fitnessCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException("健身卡不存在");
        }
        card.setStatus(card.getStatus() == 1 ? 0 : 1);
        card.setUpdateTime(LocalDateTime.now());
        fitnessCardMapper.updateById(card);
        return card;
    }
}
