package com.example.gym.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.gym.common.Response;
import com.example.gym.dto.AiChatDTO;
import com.example.gym.entity.AiChat;
import com.example.gym.service.AiChatService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    
    private final AiChatService aiChatService;
    
    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }
    
    @PostMapping("/chat")
    public Response<AiChat> createChat(@RequestBody AiChatDTO aiChatDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = 1L;
        
        AiChat aiChat = aiChatService.createChat(userId, aiChatDTO);
        return Response.success(aiChat);
    }
    
    @GetMapping("/chats")
    public Response<IPage<AiChat>> listChats(@RequestParam(defaultValue = "1") int page, 
                                             @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = 1L;
        
        IPage<AiChat> chats = aiChatService.listChats(userId, page, size);
        return Response.success(chats);
    }
}
