package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.common.Response;
import com.example.gym.dto.FitnessCardDTO;
import com.example.gym.entity.FitnessCard;
import com.example.gym.service.FitnessCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健身卡控制器
 */
@RestController
@RequestMapping("/api/card")
public class FitnessCardController {

    @Autowired
    private FitnessCardService fitnessCardService;

    /**
     * 创建健身卡
     */
    @PostMapping
    public Response<FitnessCard> createCard(@RequestBody FitnessCardDTO dto) {
        FitnessCard card = fitnessCardService.createCard(dto);
        return Response.success(card);
    }

    /**
     * 更新健身卡
     */
    @PutMapping("/{id}")
    public Response<FitnessCard> updateCard(@PathVariable Long id, @RequestBody FitnessCardDTO dto) {
        FitnessCard card = fitnessCardService.updateCard(id, dto);
        return Response.success(card);
    }

    /**
     * 删除健身卡
     */
    @DeleteMapping("/{id}")
    public Response<Void> deleteCard(@PathVariable Long id) {
        fitnessCardService.deleteCard(id);
        return Response.success(null);
    }

    /**
     * 根据ID查询健身卡
     */
    @GetMapping("/{id}")
    public Response<FitnessCard> getCardById(@PathVariable Long id) {
        FitnessCard card = fitnessCardService.getCardById(id);
        return Response.success(card);
    }

    /**
     * 分页查询所有健身卡
     */
    @GetMapping
    public Response<IPage<FitnessCard>> listCards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<FitnessCard> cardPage = fitnessCardService.listCards(new Page<>(page, size));
        return Response.success(cardPage);
    }

    /**
     * 查询所有上架的健身卡
     */
    @GetMapping("/active")
    public Response<List<FitnessCard>> listActiveCards() {
        List<FitnessCard> cards = fitnessCardService.listActiveCards();
        return Response.success(cards);
    }

    /**
     * 上下架切换
     */
    @PutMapping("/{id}/status")
    public Response<FitnessCard> toggleStatus(@PathVariable Long id) {
        FitnessCard card = fitnessCardService.toggleStatus(id);
        return Response.success(card);
    }
}
