package com.example.gym.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekService {
    
    @Value("${deepseek.api-key}")
    private String apiKey;
    
    @Value("${deepseek.base-url}")
    private String baseUrl;
    
    @Value("${deepseek.model}")
    private String model;
    
    private final RestTemplate restTemplate;
    
    public DeepSeekService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public String chat(String userMessage) {
        try {
            String url = baseUrl + "/chat/completions";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的健身顾问AI助手，专门为健身房会员提供健身指导、饮食建议、训练计划等服务。请用友好、专业的语气回答用户的问题。");
            messages.add(systemMessage);
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1024);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        return (String) message.get("content");
                    }
                }
            }
            
            return "抱歉，我暂时无法回答您的问题，请稍后再试。";
        } catch (Exception e) {
            System.err.println("DeepSeek API调用失败: " + e.getMessage());
            return "抱歉，服务暂时不可用，请稍后再试。";
        }
    }
}
