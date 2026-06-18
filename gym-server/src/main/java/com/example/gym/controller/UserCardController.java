package com.example.gym.controller;

import com.example.gym.common.Response;
import com.example.gym.entity.UserCard;
import com.example.gym.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户购卡记录控制器
 */
@RestController
@RequestMapping("/api/user-card")
public class UserCardController {

    @Autowired
    private UserCardService userCardService;

    /**
     * 用户购买健身卡
     */
    @PostMapping("/purchase")
    public Response<UserCard> purchaseCard(@RequestParam Long userId, @RequestParam Long cardId) {
        UserCard userCard = userCardService.purchaseCard(userId, cardId);
        return Response.success(userCard);
    }

    /**
     * 根据用户ID查询当前生效的卡
     */
    @GetMapping("/current/{userId}")
    public Response<UserCard> getCurrentCard(@PathVariable Long userId) {
        UserCard userCard = userCardService.getCurrentCard(userId);
        return Response.success(userCard);
    }

    /**
     * 根据用户ID查询所有购卡记录
     */
    @GetMapping("/user/{userId}")
    public Response<List<UserCard>> getUserCards(@PathVariable Long userId) {
        List<UserCard> cards = userCardService.getUserCards(userId);
        return Response.success(cards);
    }

    /**
     * 根据ID查询购卡记录
     */
    @GetMapping("/{id}")
    public Response<UserCard> getCardById(@PathVariable Long id) {
        UserCard userCard = userCardService.getCardById(id);
        return Response.success(userCard);
    }

    /**
     * 换卡：用户将当前卡更换为其他类型的卡
     */
    @PostMapping("/change")
    public Response<UserCard> changeCard(@RequestParam Long userId, @RequestParam Long newCardId) {
        UserCard userCard = userCardService.changeCard(userId, newCardId);
        return Response.success(userCard);
    }

    /**
     * 续卡：用户为当前卡续期
     */
    @PostMapping("/renew")
    public Response<UserCard> renewCard(@RequestParam Long userId, @RequestParam Long cardId) {
        UserCard userCard = userCardService.renewCard(userId, cardId);
        return Response.success(userCard);
    }
}
