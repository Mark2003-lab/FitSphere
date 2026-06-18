package com.example.gym;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.gym.mapper")
@EnableScheduling
public class GymManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymManagementApplication.class, args);
    }
}
