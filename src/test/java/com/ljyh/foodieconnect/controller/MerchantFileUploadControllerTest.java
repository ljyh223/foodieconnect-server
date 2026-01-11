package com.ljyh.foodieconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.foodieconnect.service.FileUploadService;
import com.ljyh.foodieconnect.service.JwtMerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MerchantFileUploadControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private FileUploadService fileUploadService;
    
    @Mock
    private JwtMerchantService jwtMerchantService;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private MerchantFileUploadController merchantFileUploadController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantFileUploadController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        
        // 模拟认证信息
        when(authentication.getName()).thenReturn("testmerchant");
        when(authentication.isAuthenticated()).thenReturn(true);
    }
    
    @Test
    void testUploadImageSuccess() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
        
        String imageUrl = "http://example.com/images/test.jpg";
        
        // 模拟服务调用
        when(fileUploadService.uploadImage(any(MockMultipartFile.class))).thenReturn(imageUrl);
        
        // 执行测试
        mockMvc.perform(multipart("/merchant/upload/image")
                .file(file)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.url").value(imageUrl))
                .andExpect(jsonPath("$.data.filename").value("test.jpg"))
                .andExpect(jsonPath("$.data.size").value("18"));
    }
    
    @Test
    void testUploadImageUnauthorized() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
        
        // 模拟未认证状态
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // 执行测试
        mockMvc.perform(multipart("/merchant/upload/image")
                .file(file)
                .principal(authentication))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.error.message").value("请先登录后再上传图片"));
    }
    
    @Test
    void testUploadImageInvalidFile() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test text content".getBytes()
        );
        
        // 模拟服务调用抛出异常
        when(fileUploadService.uploadImage(any(MockMultipartFile.class)))
                .thenThrow(new IllegalArgumentException("不支持的文件类型"));
        
        // 执行测试
        mockMvc.perform(multipart("/merchant/upload/image")
                .file(file)
                .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_FILE"))
                .andExpect(jsonPath("$.error.message").value("不支持的文件类型"));
    }
    
    @Test
    void testUploadImagesSuccess() throws Exception {
        // 准备测试数据
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image 1 content".getBytes()
        );
        
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image 2 content".getBytes()
        );
        
        String imageUrl1 = "http://example.com/images/test1.jpg";
        String imageUrl2 = "http://example.com/images/test2.jpg";
        
        // 模拟服务调用
        when(fileUploadService.uploadImage(any(MockMultipartFile.class)))
                .thenReturn(imageUrl1)
                .thenReturn(imageUrl2);
        
        // 执行测试
        mockMvc.perform(multipart("/merchant/upload/images")
                .file(file1)
                .file(file2)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.success.test1.jpg").value(imageUrl1))
                .andExpect(jsonPath("$.data.success.test2.jpg").value(imageUrl2))
                .andExpect(jsonPath("$.data.failed").isEmpty())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failedCount").value(0));
    }
    
    @Test
    void testUploadImagesEmpty() throws Exception {
        // 执行测试 - 不传递任何文件
        mockMvc.perform(multipart("/merchant/upload/images")
                .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NO_FILES"))
                .andExpect(jsonPath("$.error.message").value("请选择要上传的文件"));
    }
    
    @Test
    void testUploadImagesTooMany() throws Exception {
        // 准备测试数据 - 6个文件，超过限制
        MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 2 content".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("files", "test3.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 3 content".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("files", "test4.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 4 content".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("files", "test5.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 5 content".getBytes());
        MockMultipartFile file6 = new MockMultipartFile("files", "test6.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 6 content".getBytes());
        
        // 执行测试
        mockMvc.perform(multipart("/merchant/upload/images")
                .file(file1)
                .file(file2)
                .file(file3)
                .file(file4)
                .file(file5)
                .file(file6)
                .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("TOO_MANY_FILES"))
                .andExpect(jsonPath("$.error.message").value("一次最多只能上传5个文件"));
    }
    
    @Test
    void testUploadImagesPartialSuccess() throws Exception {
        // 准备测试数据
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image 1 content".getBytes()
        );
        
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test text content".getBytes()
        );
        
        String imageUrl1 = "http://example.com/images/test1.jpg";
        
        // 模拟服务调用
        when(fileUploadService.uploadImage(any(MockMultipartFile.class)))
                .thenReturn(imageUrl1)
                .thenThrow(new IllegalArgumentException("不支持的文件类型"));
        
        // 执行测试
        mockMvc.perform(multipart("/merchant/upload/images")
                .file(file1)
                .file(file2)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.success.test1.jpg").value(imageUrl1))
                .andExpect(jsonPath("$.data.failed.test2.txt").exists())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failedCount").value(1));
    }
}
