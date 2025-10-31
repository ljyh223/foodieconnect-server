package com.ljyh.tabletalk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件上传服务类
 */
@Service
public class FileUploadService {
    
    @Value("${app.upload.dir}")
    private String uploadDir;
    
    @Value("${app.upload.base-url}")
    private String baseUrl;
    
    // 允许的图片文件类型
    private static final String[] ALLOWED_FILE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };
    
    // 最大文件大小 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 上传图片文件
     * 
     * @param file 上传的文件
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     * @throws IllegalArgumentException 文件类型或大小不符合要求
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // 验证文件
        validateFile(file);
        
        // 创建上传目录（如果不存在）
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回文件相对路径
        return "/uploads/" + newFilename;
    }
    
    /**
     * 验证上传的文件
     * 
     * @param file 上传的文件
     * @throws IllegalArgumentException 文件类型或大小不符合要求
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ALLOWED_FILE_TYPES).contains(contentType)) {
            throw new IllegalArgumentException("只支持JPEG、PNG、GIF和WebP格式的图片文件");
        }
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 文件扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex);
    }
}