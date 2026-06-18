package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.entity.Coach;
import com.example.gym.entity.User;
import com.example.gym.mapper.CoachMapper;
import com.example.gym.mapper.UserMapper;
import com.example.gym.service.CoachService;
import com.example.gym.service.RedisService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoachServiceImpl implements CoachService {

    private final CoachMapper coachMapper;
    private final UserMapper userMapper;
    private final RedisService redisService;

    private static final String COACH_LIST_CACHE_KEY = "coach:list";
    private static final String COACH_DETAIL_CACHE_KEY = "coach:detail";

    public CoachServiceImpl(CoachMapper coachMapper, UserMapper userMapper, RedisService redisService) {
        this.coachMapper = coachMapper;
        this.userMapper = userMapper;
        this.redisService = redisService;
    }

    @Override
    @CacheEvict(value = {COACH_LIST_CACHE_KEY, COACH_DETAIL_CACHE_KEY}, allEntries = true)
    public Coach addCoach(Coach coach) {
        coachMapper.insert(coach);
        return coach;
    }

    @Override
    @CacheEvict(value = {COACH_LIST_CACHE_KEY, COACH_DETAIL_CACHE_KEY}, allEntries = true)
    public Coach updateCoach(Long id, Coach coach) {
        Coach existing = coachMapper.selectById(id);
        if (existing != null) {
            existing.setName(coach.getName());
            existing.setPhone(coach.getPhone());
            existing.setSpeciality(coach.getSpeciality());
            existing.setDescription(coach.getDescription());
            existing.setPrice(coach.getPrice());
            existing.setCertification(coach.getCertification());
            existing.setExperienceYears(coach.getExperienceYears());
            existing.setAvatar(coach.getAvatar());
            coachMapper.updateById(existing);
        }
        return existing;
    }

    @Override
    @CacheEvict(value = {COACH_LIST_CACHE_KEY, COACH_DETAIL_CACHE_KEY}, allEntries = true)
    public void deleteCoach(Long id) {
        coachMapper.deleteById(id);
    }

    @Override
    @Cacheable(value = COACH_DETAIL_CACHE_KEY, key = "'id:' + #id")
    public Coach getCoachById(Long id) {
        return coachMapper.selectById(id);
    }

    @Override
    @Cacheable(value = COACH_DETAIL_CACHE_KEY, key = "'phone:' + #phone")
    public Coach getCoachByPhone(String phone) {
        if (phone == null || phone.isEmpty()) return null;
        return coachMapper.selectOne(Wrappers.<Coach>lambdaQuery()
                .eq(Coach::getPhone, phone)
                .last("LIMIT 1"));
    }

    @Override
    @Cacheable(value = COACH_LIST_CACHE_KEY, key = "'page:' + #page + ':' + #size")
    public IPage<Coach> listCoaches(int page, int size) {
        return coachMapper.selectPage(new Page<>(page, size), Wrappers.emptyWrapper());
    }

    @Override
    @Cacheable(value = COACH_LIST_CACHE_KEY, key = "'all'")
    public List<Coach> listAllCoaches() {
        List<Coach> coaches = coachMapper.selectList(Wrappers.emptyWrapper());
        // 始终从用户表同步头像，确保教练修改头像后实时生效
        Set<Long> userIds = coaches.stream()
                .map(Coach::getUserId)
                .filter(uid -> uid != null)
                .collect(Collectors.toSet());
        if (!userIds.isEmpty()) {
            Map<Long, String> userAvatars = userMapper.selectBatchIds(userIds).stream()
                    .filter(u -> u.getAvatar() != null && !u.getAvatar().isEmpty())
                    .collect(Collectors.toMap(User::getId, User::getAvatar));
            coaches.forEach(c -> {
                if (c.getUserId() != null) {
                    String userAvatar = userAvatars.get(c.getUserId());
                    if (userAvatar != null) {
                        c.setAvatar(userAvatar);
                    }
                }
            });
        }
        return coaches;
    }
}
