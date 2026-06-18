package com.example.gym.controller;

import com.example.gym.common.Response;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {
    
    private final String uploadDir;
    
    public FileController() {
        // 获取项目根目录下的 uploads/avatars 目录（相对于 gym-server 的父目录）
        String projectRoot = System.getProperty("user.dir");
        // 头像文件实际存储在项目根目录的 uploads/avatars 下，不是 gym-server 目录下
        this.uploadDir = new File(projectRoot).getParent() + File.separator + "uploads" + File.separator + "avatars" + File.separator;
        
        // 确保目录存在
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
    }
    
    @PostMapping("/upload/avatar")
    public Response<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Response.error("请选择要上传的文件");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Response.error("请上传图片文件");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        
        try {
            File dest = new File(uploadDir + newFilename);
            file.transferTo(dest);
            
            String filePath = "/api/file/avatar/" + newFilename;
            Map<String, String> result = new HashMap<>();
            result.put("url", filePath);
            return Response.success("上传成功", result);
        } catch (IOException e) {
            return Response.error("上传失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/avatar/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        try {
            File file = new File(uploadDir + filename);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
