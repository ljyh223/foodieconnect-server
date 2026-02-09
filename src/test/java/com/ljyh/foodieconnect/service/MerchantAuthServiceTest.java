package com.ljyh.foodieconnect.service;

import com.ljyh.foodieconnect.dto.MerchantRegisterRequest;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.ChatRoomMapper;
import com.ljyh.foodieconnect.mapper.MerchantMapper;
import com.ljyh.foodieconnect.mapper.RestaurantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class MerchantAuthServiceTest {

    @Mock
    private MerchantUserDetailsServiceImpl merchantUserDetailsService;

    @Mock
    private JwtMerchantService jwtMerchantService;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private ChatRoomMapper chatRoomMapper;

    @InjectMocks
    private MerchantAuthService merchantAuthService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testLoginSuccess() {
        // 准备测试数据
        String username = "testmerchant";
        String password = "password123";
        String passwordHash = "$2a$10$testpasswordhash";
        
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername(username);
        merchant.setPasswordHash(passwordHash);
        merchant.setStatus(Merchant.MerchantStatus.ACTIVE);
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(username)
                .password(passwordHash)
                .authorities("ROLE_ADMIN")
                .build();
        
        // 模拟服务调用
        when(merchantMapper.findByUsername(username)).thenReturn(merchant);
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(merchantUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtMerchantService.generateToken(merchant)).thenReturn("test-token");
        
        // 执行测试
        MerchantAuthService.MerchantLoginResult result = merchantAuthService.login(username, password);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        assertEquals(merchant, result.getMerchant());
    }
    
    @Test
    void testLoginFailure_MerchantNotFound() {
        // 准备测试数据
        String username = "nonexistentmerchant";
        String password = "password123";
        
        // 模拟服务调用
        when(merchantMapper.findByUsername(username)).thenReturn(null);
        
        // 执行测试并验证结果
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> merchantAuthService.login(username, password));
        assertEquals("MERCHANT_NOT_FOUND", exception.getCode());
        assertEquals("商家不存在", exception.getMessage());
    }
    
    @Test
    void testLoginFailure_MerchantDisabled() {
        // 准备测试数据
        String username = "disabledmerchant";
        String password = "password123";
        
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername(username);
        merchant.setPasswordHash("$2a$10$testpasswordhash");
        merchant.setStatus(Merchant.MerchantStatus.BANNED);
        
        // 模拟服务调用
        when(merchantMapper.findByUsername(username)).thenReturn(merchant);
        
        // 执行测试并验证结果
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> merchantAuthService.login(username, password));
        assertEquals("MERCHANT_DISABLED", exception.getCode());
        assertEquals("商家账户已被禁用", exception.getMessage());
    }
    
    @Test
    void testLoginFailure_WrongPassword() {
        // 准备测试数据
        String username = "testmerchant";
        String password = "wrongpassword";
        String passwordHash = "$2a$10$testpasswordhash";
        
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername(username);
        merchant.setPasswordHash(passwordHash);
        merchant.setStatus(Merchant.MerchantStatus.ACTIVE);
        
        // 模拟服务调用
        when(merchantMapper.findByUsername(username)).thenReturn(merchant);
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(false);
        
        // 执行测试并验证结果
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> merchantAuthService.login(username, password));
        assertEquals("LOGIN_FAILED", exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
    }
    
    @Test
    void testRegisterSuccess() {
        // 准备测试数据
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("newmerchant");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setName("New Merchant");
        request.setPhone("13800138000");
        request.setRestaurantName("测试餐厅");
        request.setRestaurantType("中餐");
        request.setRestaurantAddress("北京市朝阳区");
        request.setRestaurantImage("http://example.com/image.jpg");

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername(request.getUsername());
        merchant.setEmail(request.getEmail());
        merchant.setPasswordHash("$2a$10$testpasswordhash");
        merchant.setName(request.getName());
        merchant.setPhone(request.getPhone());
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.ADMIN);
        merchant.setStatus(Merchant.MerchantStatus.ACTIVE);

        com.ljyh.foodieconnect.entity.Restaurant restaurant = new com.ljyh.foodieconnect.entity.Restaurant();
        restaurant.setId(1L);
        restaurant.setName(request.getRestaurantName());
        restaurant.setType(request.getRestaurantType());
        restaurant.setAddress(request.getRestaurantAddress());
        restaurant.setImageUrl(request.getRestaurantImage());

        // 模拟服务调用
        when(merchantMapper.findByUsername(request.getUsername())).thenReturn(null);
        when(merchantMapper.findByEmail(request.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$testpasswordhash");
        when(restaurantMapper.insert(any(com.ljyh.foodieconnect.entity.Restaurant.class))).thenAnswer(invocation -> {
            com.ljyh.foodieconnect.entity.Restaurant insertedRestaurant = invocation.getArgument(0);
            insertedRestaurant.setId(1L);
            return 1;
        });
        when(merchantMapper.insert(any(Merchant.class))).thenAnswer(invocation -> {
            Merchant insertedMerchant = invocation.getArgument(0);
            insertedMerchant.setId(1L);
            return 1;
        });
        when(chatRoomMapper.insert(any(com.ljyh.foodieconnect.entity.ChatRoom.class))).thenReturn(1);

        // 执行测试
        Merchant result = merchantAuthService.register(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(request.getUsername(), result.getUsername());
        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPhone(), result.getPhone());
        assertEquals(1L, result.getRestaurantId());
        assertEquals(Merchant.MerchantRole.ADMIN, result.getRole());
        assertEquals(Merchant.MerchantStatus.ACTIVE, result.getStatus());
    }
    
    @Test
    void testRegisterFailure_UsernameExists() {
        // 准备测试数据
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("existingmerchant");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setName("New Merchant");
        request.setPhone("13800138000");
        request.setRestaurantName("测试餐厅");
        request.setRestaurantType("中餐");
        request.setRestaurantAddress("北京市朝阳区");

        Merchant existingMerchant = new Merchant();
        existingMerchant.setId(1L);
        existingMerchant.setUsername(request.getUsername());

        // 模拟服务调用
        when(merchantMapper.findByUsername(request.getUsername())).thenReturn(existingMerchant);

        // 执行测试并验证结果
        BusinessException exception = assertThrows(BusinessException.class,
            () -> merchantAuthService.register(request));
        assertEquals("USERNAME_EXISTS", exception.getCode());
        assertEquals("用户名已存在", exception.getMessage());
    }
    
    @Test
    void testRegisterFailure_EmailExists() {
        // 准备测试数据
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("newmerchant");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setName("New Merchant");
        request.setPhone("13800138000");
        request.setRestaurantName("测试餐厅");
        request.setRestaurantType("中餐");
        request.setRestaurantAddress("北京市朝阳区");

        Merchant existingMerchant = new Merchant();
        existingMerchant.setId(1L);
        existingMerchant.setEmail(request.getEmail());

        // 模拟服务调用
        when(merchantMapper.findByUsername(request.getUsername())).thenReturn(null);
        when(merchantMapper.findByEmail(request.getEmail())).thenReturn(existingMerchant);

        // 执行测试并验证结果
        BusinessException exception = assertThrows(BusinessException.class,
            () -> merchantAuthService.register(request));
        assertEquals("EMAIL_EXISTS", exception.getCode());
        assertEquals("邮箱已存在", exception.getMessage());
    }
    
    @Test
    void testChangePasswordSuccess() {
        // 准备测试数据
        String username = "testmerchant";
        String oldPassword = "oldpassword";
        String newPassword = "newpassword";
        String oldPasswordHash = "$2a$10$oldpasswordhash";
        String newPasswordHash = "$2a$10$newpasswordhash";
        
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername(username);
        merchant.setPasswordHash(oldPasswordHash);
        
        // 模拟服务调用
        when(merchantMapper.findByUsername(username)).thenReturn(merchant);
        when(passwordEncoder.matches(oldPassword, oldPasswordHash)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newPasswordHash);
        when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> merchantAuthService.changePassword(username, oldPassword, newPassword));
    }
    
    @Test
    void testChangePasswordFailure_MerchantNotFound() {
        // 准备测试数据
        String username = "nonexistentmerchant";
        String oldPassword = "oldpassword";
        String newPassword = "newpassword";
        
        // 模拟服务调用
        when(merchantMapper.findByUsername(username)).thenReturn(null);
        
        // 执行测试并验证结果
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> merchantAuthService.changePassword(username, oldPassword, newPassword));
        assertEquals("MERCHANT_NOT_FOUND", exception.getCode());
        assertEquals("商家不存在", exception.getMessage());
    }
    
    @Test
    void testChangePasswordFailure_WrongOldPassword() {
        // 准备测试数据
        String username = "testmerchant";
        String oldPassword = "wrongoldpassword";
        String newPassword = "newpassword";
        String oldPasswordHash = "$2a$10$oldpasswordhash";
        
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername(username);
        merchant.setPasswordHash(oldPasswordHash);
        
        // 模拟服务调用
        when(merchantMapper.findByUsername(username)).thenReturn(merchant);
        when(passwordEncoder.matches(oldPassword, oldPasswordHash)).thenReturn(false);
        
        // 执行测试并验证结果
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> merchantAuthService.changePassword(username, oldPassword, newPassword));
        assertEquals("INVALID_OLD_PASSWORD", exception.getCode());
        assertEquals("旧密码错误", exception.getMessage());
    }
}
