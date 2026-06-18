package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Integer quantity;
    
    private String status;
}
