package com.example.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gym.entity.UserCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户购卡记录Mapper
 */
@Mapper
public interface UserCardMapper extends BaseMapper<UserCard> {

    /**
     * 根据用户ID查询当前生效的卡
     */
    UserCard selectCurrentCardByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询所有购卡记录
     */
    List<UserCard> selectByUserId(@Param("userId") Long userId);
}
