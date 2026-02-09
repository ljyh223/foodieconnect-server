package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.DishReviewResponse;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.service.DishReviewService;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商家端菜品评价控制器测试类
 */
class MerchantDishReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DishReviewService dishReviewService;

    @Mock
    private MerchantAuthService merchantAuthService;

    @InjectMocks
    private MerchantDishReviewController merchantDishReviewController;

    private Merchant testMerchant;
    private DishReviewResponse testReviewResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantDishReviewController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();

        // 初始化测试数据 - 商家
        testMerchant = new Merchant();
        testMerchant.setId(1L);
        testMerchant.setRestaurantId(1L);
        testMerchant.setRole(Merchant.MerchantRole.ADMIN);
        testMerchant.setUsername("testmerchant");

        // 初始化测试数据 - 评价响应
        testReviewResponse = new DishReviewResponse();
        testReviewResponse.setId(1L);
        testReviewResponse.setMenuItemId(1L);
        testReviewResponse.setItemName("宫保鸡丁");
        testReviewResponse.setItemPrice(new BigDecimal("38.00"));
        testReviewResponse.setItemImage("/uploads/dish1.jpg");
        testReviewResponse.setUserId(2L);
        testReviewResponse.setUserName("顾客A");
        testReviewResponse.setUserAvatar("/avatar/customer.jpg");
        testReviewResponse.setRating(5);
        testReviewResponse.setComment("很好吃！");
        testReviewResponse.setImages(Arrays.asList("/uploads/review1.jpg"));
        testReviewResponse.setCreatedAt(LocalDateTime.now().toString());

        // 模拟商家认证
        when(merchantAuthService.getCurrentMerchant()).thenReturn(testMerchant);
    }

    @Test
    void testGetItemReviewsSuccess() throws Exception {
        // 准备测试数据
        Page<DishReviewResponse> page = new Page<>(0, 20, 1);
        page.setRecords(Arrays.asList(testReviewResponse));

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), eq(1L), eq(0), eq(20), isNull()))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/1/reviews")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].id").value(1L))
                .andExpect(jsonPath("$.data.records[0].rating").value(5))
                .andExpect(jsonPath("$.data.records[0].itemName").value("宫保鸡丁"));
    }

    @Test
    void testGetItemReviewsWithRatingFilter() throws Exception {
        // 准备测试数据 - 只返回5星评价
        Page<DishReviewResponse> page = new Page<>(0, 20, 5);
        page.setRecords(Arrays.asList(testReviewResponse));

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), eq(1L), eq(0), eq(20), eq(5)))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/1/reviews")
                        .param("page", "0")
                        .param("size", "20")
                        .param("rating", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(5));
    }

    @Test
    void testGetItemReviewsWithLowRatingFilter() throws Exception {
        // 准备测试数据 - 1星评价
        Page<DishReviewResponse> page = new Page<>(0, 20, 2);

        DishReviewResponse lowRatingResponse = new DishReviewResponse();
        lowRatingResponse.setId(2L);
        lowRatingResponse.setMenuItemId(1L);
        lowRatingResponse.setItemName("宫保鸡丁");
        lowRatingResponse.setRating(1);
        lowRatingResponse.setComment("很难吃！");

        page.setRecords(Arrays.asList(lowRatingResponse));

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), eq(1L), eq(0), eq(20), eq(1)))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/1/reviews")
                        .param("page", "0")
                        .param("size", "20")
                        .param("rating", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].rating").value(1));
    }

    @Test
    void testGetItemReviewsDefaultParameters() throws Exception {
        // 准备测试数据
        Page<DishReviewResponse> page = new Page<>(0, 20, 0);

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), eq(1L), eq(0), eq(20), isNull()))
                .thenReturn(page);

        // 执行测试 - 使用默认参数
        mockMvc.perform(get("/merchant/menu-items/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetReviewOverviewSuccess() throws Exception {
        // 准备概览数据
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalReviews", 500);
        overview.put("averageRating", 4.3);

        Map<String, Object> topRatedItem = new HashMap<>();
        topRatedItem.put("menuItemId", 1L);
        topRatedItem.put("itemName", "宫保鸡丁");
        topRatedItem.put("averageRating", 4.8);
        topRatedItem.put("reviewCount", 50);

        Map<String, Object> lowRatedItem = new HashMap<>();
        lowRatedItem.put("menuItemId", 3L);
        lowRatedItem.put("itemName", "水煮鱼");
        lowRatedItem.put("averageRating", 2.3);
        lowRatedItem.put("reviewCount", 10);

        overview.put("topRatedItems", Arrays.asList(topRatedItem));
        overview.put("lowRatedItems", Arrays.asList(lowRatedItem));

        // 模拟服务调用
        when(dishReviewService.getMerchantReviewOverview(eq(1L))).thenReturn(overview);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/reviews/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalReviews").value(500))
                .andExpect(jsonPath("$.data.averageRating").value(4.3))
                .andExpect(jsonPath("$.data.topRatedItems[0].itemName").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data.topRatedItems[0].averageRating").value(4.8))
                .andExpect(jsonPath("$.data.lowRatedItems[0].itemName").value("水煮鱼"))
                .andExpect(jsonPath("$.data.lowRatedItems[0].averageRating").value(2.3));
    }

    @Test
    void testGetReviewOverviewNoReviews() throws Exception {
        // 准备空概览数据
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalReviews", 0);
        overview.put("averageRating", 0.0);
        overview.put("topRatedItems", Arrays.asList());
        overview.put("lowRatedItems", Arrays.asList());

        // 模拟服务调用
        when(dishReviewService.getMerchantReviewOverview(eq(1L))).thenReturn(overview);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/reviews/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalReviews").value(0))
                .andExpect(jsonPath("$.data.averageRating").value(0.0))
                .andExpect(jsonPath("$.data.topRatedItems").isArray())
                .andExpect(jsonPath("$.data.lowRatedItems").isArray());
    }

    @Test
    void testGetReviewOverviewWithMultipleItems() throws Exception {
        // 准备包含多个菜品的概览数据
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalReviews", 300);
        overview.put("averageRating", 4.2);

        List<Map<String, Object>> topRatedItems = Arrays.asList(
                createItemStats(1L, "宫保鸡丁", 4.8, 60),
                createItemStats(2L, "鱼香肉丝", 4.6, 45),
                createItemStats(4L, "麻婆豆腐", 4.5, 40)
        );

        List<Map<String, Object>> lowRatedItems = Arrays.asList(
                createItemStats(5L, "凉拌黄瓜", 2.2, 10),
                createItemStats(6L, "酸辣汤", 2.5, 8)
        );

        overview.put("topRatedItems", topRatedItems);
        overview.put("lowRatedItems", lowRatedItems);

        // 模拟服务调用
        when(dishReviewService.getMerchantReviewOverview(eq(1L))).thenReturn(overview);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/reviews/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.topRatedItems.length()").value(3))
                .andExpect(jsonPath("$.data.lowRatedItems.length()").value(2))
                .andExpect(jsonPath("$.data.topRatedItems[0].itemName").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data.topRatedItems[1].itemName").value("鱼香肉丝"));
    }

    @Test
    void testGetAllReviewsSuccess() throws Exception {
        // 准备测试数据 - 所有菜品的评价
        Page<DishReviewResponse> page = new Page<>(0, 20, 3);
        page.setRecords(Arrays.asList(
                testReviewResponse,
                createReviewResponse(2L, 2L, "鱼香肉丝", 4),
                createReviewResponse(3L, 3L, "水煮鱼", 5)
        ));

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), isNull(), eq(0), eq(20), isNull()))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/reviews/all")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.records[0].itemName").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data.records[1].itemName").value("鱼香肉丝"))
                .andExpect(jsonPath("$.data.records[2].itemName").value("水煮鱼"));
    }

    @Test
    void testGetAllReviewsWithRatingFilter() throws Exception {
        // 准备测试数据 - 只返回差评
        Page<DishReviewResponse> page = new Page<>(0, 20, 5);

        DishReviewResponse badReview1 = createReviewResponse(1L, 1L, "宫保鸡丁", 1);
        badReview1.setComment("很难吃！");
        DishReviewResponse badReview2 = createReviewResponse(2L, 2L, "鱼香肉丝", 2);
        badReview2.setComment("不新鲜");

        page.setRecords(Arrays.asList(badReview1, badReview2));

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), isNull(), eq(0), eq(20), eq(1)))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/reviews/all")
                        .param("page", "0")
                        .param("size", "20")
                        .param("rating", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].rating").value(1))
                .andExpect(jsonPath("$.data.records[0].comment").value("很难吃！"));
    }

    @Test
    void testGetAllReviewsPagination() throws Exception {
        // 准备测试数据 - 第2页
        Page<DishReviewResponse> page = new Page<>(1, 10, 25);
        page.setRecords(Arrays.asList(testReviewResponse));

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), isNull(), eq(1), eq(10), isNull()))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/reviews/all")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").value(25));
    }

    @Test
    void testGetItemReviewsDifferentMerchant() throws Exception {
        // 准备另一个商家的数据
        Merchant otherMerchant = new Merchant();
        otherMerchant.setId(2L);
        otherMerchant.setRestaurantId(2L);
        otherMerchant.setRole(Merchant.MerchantRole.ADMIN);

        when(merchantAuthService.getCurrentMerchant()).thenReturn(otherMerchant);

        // 准备测试数据
        Page<DishReviewResponse> page = new Page<>(0, 20, 1);
        page.setRecords(Arrays.asList(testReviewResponse));

        // 模拟服务调用 - 应该查询餐厅ID为2的数据
        when(dishReviewService.getMerchantItemReviews(eq(2L), eq(1L), eq(0), eq(20), isNull()))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetItemReviewsEmpty() throws Exception {
        // 准备测试数据 - 空结果
        Page<DishReviewResponse> page = new Page<>(0, 20, 0);
        page.setRecords(Arrays.asList());

        // 模拟服务调用
        when(dishReviewService.getMerchantItemReviews(eq(1L), eq(1L), eq(0), eq(20), isNull()))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/merchant/menu-items/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    // 辅助方法
    private Map<String, Object> createItemStats(Long id, String name, double rating, int count) {
        Map<String, Object> item = new HashMap<>();
        item.put("menuItemId", id);
        item.put("itemName", name);
        item.put("averageRating", rating);
        item.put("reviewCount", count);
        return item;
    }

    private DishReviewResponse createReviewResponse(Long id, Long menuItemId, String itemName, int rating) {
        DishReviewResponse response = new DishReviewResponse();
        response.setId(id);
        response.setMenuItemId(menuItemId);
        response.setItemName(itemName);
        response.setRating(rating);
        response.setUserId(2L);
        response.setUserName("顾客A");
        response.setCreatedAt(LocalDateTime.now().toString());
        return response;
    }
}
