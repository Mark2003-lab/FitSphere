package com.example.gym.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录下的 uploads/avatars 目录（相对于 gym-server 的父目录）
        String projectRoot = System.getProperty("user.dir");
        // 头像文件实际存储在项目根目录的 uploads/avatars 下，不是 gym-server 目录下
        String uploadDir = new File(projectRoot).getParent() + File.separator + "uploads" + File.separator + "avatars" + File.separator;
        
        // 映射 /api/file/avatar/ 到 uploads/avatars/ 目录
        registry.addResourceHandler("/api/file/avatar/**")
                .addResourceLocations("file:" + uploadDir);
    }
}
