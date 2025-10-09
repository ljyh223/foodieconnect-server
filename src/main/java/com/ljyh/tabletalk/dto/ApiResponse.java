package com.ljyh.tabletalk.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一API响应格式
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 错误信息
     */
    private ErrorInfo error;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * 成功响应（无数据）
     */
    public static ApiResponse<Void> success() {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * 错误响应
     */
    public static ApiResponse<Void> error(String code, String message) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(new ErrorInfo(code, message));
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * 错误响应（带详情）
     */
    public static ApiResponse<Void> error(String code, String message, String details) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(new ErrorInfo(code, message, details));
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * 错误信息类
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorInfo {
        private String code;
        private String message;
        private String details;
        
        public ErrorInfo(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public ErrorInfo(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }
    }
}