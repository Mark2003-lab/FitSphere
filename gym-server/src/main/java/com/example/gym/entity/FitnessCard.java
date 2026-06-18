package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健身卡实体类
 */
@Data
@TableName("fitness_card")
public class FitnessCard {

    @TableId(type = IdType.AUTO)
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
     * 首充赠送私教兑换券数量
     */
    private Integer giftCouponCount;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
