package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传", description = "文件上传相关接口")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    @Operation(summary = "上传图片", description = "上传图片文件，支持JPEG、PNG、GIF和WebP格式，最大10MB")
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证用户是否已登录
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("UNAUTHORIZED", "请先登录后再上传图片"));
            }
            
            // 上传文件
            String imageUrl = fileUploadService.uploadImage(file);
            
            // 构造返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("filename", file.getOriginalFilename());
            result.put("size", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_FILE", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("UPLOAD_FAILED", "文件上传失败：" + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SERVER_ERROR", "服务器内部错误"));
        }
    }
    
    @Operation(summary = "批量上传图片", description = "批量上传图片文件，最多支持5个文件")
    @PostMapping("/images")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            // 验证用户是否已登录
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("UNAUTHORIZED", "请先登录后再上传图片"));
            }
            
            // 验证文件数量
            if (files.length == 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("NO_FILES", "请选择要上传的文件"));
            }
            
            if (files.length > 5) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("TOO_MANY_FILES", "一次最多只能上传5个文件"));
            }
            
            // 批量上传文件
            Map<String, Object> result = new HashMap<>();
            Map<String, String> successFiles = new HashMap<>();
            Map<String, String> failedFiles = new HashMap<>();
            
            for (MultipartFile file : files) {
                try {
                    String imageUrl = fileUploadService.uploadImage(file);
                    successFiles.put(file.getOriginalFilename(), imageUrl);
                } catch (Exception e) {
                    failedFiles.put(file.getOriginalFilename(), e.getMessage());
                }
            }
            
            result.put("success", successFiles);
            result.put("failed", failedFiles);
            result.put("total", files.length);
            result.put("successCount", successFiles.size());
            result.put("failedCount", failedFiles.size());
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SERVER_ERROR", "服务器内部错误"));
        }
    }
}