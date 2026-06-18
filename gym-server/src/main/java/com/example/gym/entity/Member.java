package com.example.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("member")
public class Member {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String name;

    private String gender;

    private String phone;

    private String level;

    private String avatar;

    private LocalDateTime joinDate;

    private LocalDateTime expireTime;
}
