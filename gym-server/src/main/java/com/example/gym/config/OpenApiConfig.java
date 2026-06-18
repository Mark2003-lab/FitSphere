package com.example.gym.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI gymManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能健身房管理系统 API")
                        .description("智能健身房管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gym Management")
                                .email("support@gym.com")));
    }
}
