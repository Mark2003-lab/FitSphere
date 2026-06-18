package com.example.gym.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 健身卡DTO
 */
@Data
public class FitnessCardDTO {

    private Long id;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡类型：MONTHLY(月卡), QUARTERLY(季卡), YEARLY(年卡)
     */
    private String cardType;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 有效期（天数）
     */
    private Integer validDays;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 首充赠送私教兑换券数量
     */
    private Integer giftCouponCount;
}
