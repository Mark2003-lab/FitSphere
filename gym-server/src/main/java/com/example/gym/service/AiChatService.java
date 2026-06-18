package com.example.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.dto.AiChatDTO;
import com.example.gym.entity.AiChat;

public interface AiChatService {
    AiChat createChat(Long userId, AiChatDTO aiChatDTO);
    IPage<AiChat> listChats(Long userId, int page, int size);
}
