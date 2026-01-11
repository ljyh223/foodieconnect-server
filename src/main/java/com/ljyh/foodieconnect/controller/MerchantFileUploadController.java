package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.service.FileUploadService;
import com.ljyh.foodieconnect.service.JwtMerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户端文件上传控制器
 * 专门处理商家用户的文件上传请求
 */
@Tag(name = "商户端文件上传", description = "商户端文件上传相关接口")
@Slf4j
@RestController
@RequestMapping("/merchant/upload")
@RequiredArgsConstructor
public class MerchantFileUploadController {
    
    private final FileUploadService fileUploadService;
    private final JwtMerchantService jwtMerchantService;
    
    @Operation(summary = "商户上传图片", description = "商户上传图片文件，支持JPEG、PNG、GIF和WebP格式，最大10MB")
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            log.info("商户端文件上传请求 - 用户: {}, 文件名: {}, 大小: {}", 
                    authentication.getName(), file.getOriginalFilename(), file.getSize());
            
            // 验证商户认证状态
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("商户端文件上传失败 - 用户未认证");
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
            
            // 添加商户相关信息
            try {
                String token = getTokenFromRequest();
                if (token != null) {
                    Long merchantId = jwtMerchantService.extractMerchantId(token);
                    Long restaurantId = jwtMerchantService.extractRestaurantId(token);
                    result.put("merchantId", merchantId != null ? merchantId.toString() : null);
                    result.put("restaurantId", restaurantId != null ? restaurantId.toString() : null);
                }
            } catch (Exception e) {
                log.debug("提取商户信息失败: {}", e.getMessage());
            }
            
            log.info("商户端文件上传成功 - 用户: {}, 文件URL: {}", authentication.getName(), imageUrl);
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (IllegalArgumentException e) {
            log.warn("商户端文件上传失败 - 文件验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_FILE", e.getMessage()));
        } catch (IOException e) {
            log.error("商户端文件上传失败 - IO异常: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("UPLOAD_FAILED", "文件上传失败：" + e.getMessage()));
        } catch (Exception e) {
            log.error("商户端文件上传失败 - 服务器错误: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SERVER_ERROR", "服务器内部错误"));
        }
    }
    
    @Operation(summary = "商户批量上传图片", description = "商户批量上传图片文件，最多支持5个文件")
    @PostMapping("/images")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImages(
            @RequestParam("files") MultipartFile[] files,
            Authentication authentication) {
        try {
            log.info("商户端批量文件上传请求 - 用户: {}, 文件数量: {}", 
                    authentication.getName(), files.length);
            
            // 验证商户认证状态
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("商户端批量文件上传失败 - 用户未认证");
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
                    log.debug("商户端文件上传成功 - 文件: {}, URL: {}", file.getOriginalFilename(), imageUrl);
                } catch (Exception e) {
                    failedFiles.put(file.getOriginalFilename(), e.getMessage());
                    log.warn("商户端文件上传失败 - 文件: {}, 错误: {}", file.getOriginalFilename(), e.getMessage());
                }
            }
            
            result.put("success", successFiles);
            result.put("failed", failedFiles);
            result.put("total", files.length);
            result.put("successCount", successFiles.size());
            result.put("failedCount", failedFiles.size());
            
            // 添加商户相关信息
            try {
                String token = getTokenFromRequest();
                if (token != null) {
                    Long merchantId = jwtMerchantService.extractMerchantId(token);
                    Long restaurantId = jwtMerchantService.extractRestaurantId(token);
                    result.put("merchantId", merchantId != null ? merchantId.toString() : null);
                    result.put("restaurantId", restaurantId != null ? restaurantId.toString() : null);
                }
            } catch (Exception e) {
                log.debug("提取商户信息失败: {}", e.getMessage());
            }
            
            log.info("商户端批量文件上传完成 - 用户: {}, 成功: {}, 失败: {}", 
                    authentication.getName(), successFiles.size(), failedFiles.size());
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("商户端批量文件上传失败 - 服务器错误: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SERVER_ERROR", "服务器内部错误"));
        }
    }
    
    /**
     * 从请求中获取JWT token
     * 注意：这个方法仅用于提取商户信息，实际认证由过滤器处理
     */
    private String getTokenFromRequest() {
        // 注意：这里不能直接注入HttpServletRequest，因为在Controller中可能无法获取
        // 实际使用时，可以通过参数传递或者从SecurityContext中获取
        try {
            // 从SecurityContext中获取认证信息
            Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getDetails() instanceof org.springframework.security.web.authentication.WebAuthenticationDetails) {
                // 这里无法直接获取token，需要通过其他方式
                // 暂时返回null，商户信息可以从Authentication中获取
            }
            return null;
        } catch (Exception e) {
            log.debug("获取token失败: {}", e.getMessage());
            return null;
        }
    }
}