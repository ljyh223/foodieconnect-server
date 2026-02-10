package com.ljyh.foodieconnect.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    // 允许的图片文件类型（Content-Type）
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    // 文件魔数（文件头签名）用于更精确的文件类型检测
    private static final Map<String, String> FILE_MAGIC_NUMBERS = new HashMap<>();
    static {
        // PNG: 89 50 4E 47
        FILE_MAGIC_NUMBERS.put("89504E47", "image/png");
        // JPEG: FF D8 FF
        FILE_MAGIC_NUMBERS.put("FFD8FF", "image/jpeg");
        // GIF: 47 49 46 38
        FILE_MAGIC_NUMBERS.put("47494638", "image/gif");
        // WebP: 52 49 46 46
        FILE_MAGIC_NUMBERS.put("52494646", "image/webp");
    }

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

        // 先检查 Content-Type
        String contentType = file.getContentType();

        // 如果 Content-Type 不在允许列表中，尝试通过文件头检测
        if (contentType == null || !Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType)) {
            try {
                String detectedType = detectFileTypeByMagicNumber(file);
                if (detectedType == null || !Arrays.asList(ALLOWED_CONTENT_TYPES).contains(detectedType)) {
                    throw new IllegalArgumentException("只支持JPEG、PNG、GIF和WebP格式的图片文件");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("无法识别文件类型，请确保上传的是有效的图片文件");
            }
        }
    }

    /**
     * 通过文件头魔数检测文件类型
     *
     * @param file 上传的文件
     * @return 文件的 MIME 类型
     * @throws IOException 读取文件失败
     */
    private String detectFileTypeByMagicNumber(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        byte[] header = new byte[4];
        int bytesRead = inputStream.read(header);
        inputStream.close();

        if (bytesRead < 4) {
            return null;
        }

        // 将字节转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : header) {
            hexString.append(String.format("%02X", b));
        }

        String hex = hexString.toString();

        // 检查文件签名
        for (Map.Entry<String, String> entry : FILE_MAGIC_NUMBERS.entrySet()) {
            if (hex.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
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