package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("coach")
public class Coach {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;
    
    @TableField("name")
    private String name;
    
    @TableField("phone")
    private String phone;
    
    @TableField("speciality")
    private String speciality;

    @TableField("description")
    private String description;

    @TableField("price")
    private BigDecimal price;

    @TableField("rating")
    private BigDecimal rating;

    @TableField("total_reviews")
    private Integer totalReviews;

    @TableField("avatar")
    private String avatar;

    @TableField("certification")
    private String certification;

    @TableField("experience_years")
    private Integer experienceYears;
}
