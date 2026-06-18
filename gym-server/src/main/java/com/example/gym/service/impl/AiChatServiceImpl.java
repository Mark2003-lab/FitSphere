package com.example.gym.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gym.dto.AiChatDTO;
import com.example.gym.entity.AiChat;
import com.example.gym.mapper.AiChatMapper;
import com.example.gym.service.AiChatService;
import com.example.gym.service.DeepSeekService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AiChatServiceImpl implements AiChatService {
    
    private final AiChatMapper aiChatMapper;
    private final DeepSeekService deepSeekService;
    
    public AiChatServiceImpl(AiChatMapper aiChatMapper, DeepSeekService deepSeekService) {
        this.aiChatMapper = aiChatMapper;
        this.deepSeekService = deepSeekService;
    }
    
    @Override
    public AiChat createChat(Long userId, AiChatDTO aiChatDTO) {
        AiChat aiChat = new AiChat();
        aiChat.setUserId(userId);
        aiChat.setQuestion(aiChatDTO.getQuestion());
        
        String answer = deepSeekService.chat(aiChatDTO.getQuestion());
        aiChat.setAnswer(answer);
        aiChat.setCreateTime(LocalDateTime.now());
        
        aiChatMapper.insert(aiChat);
        return aiChat;
    }
    
    @Override
    public IPage<AiChat> listChats(Long userId, int page, int size) {
        return aiChatMapper.selectPage(new Page<>(page, size), 
                Wrappers.lambdaQuery(AiChat.class).eq(AiChat::getUserId, userId));
    }
}
