package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.DishReviewRequest;
import com.ljyh.foodieconnect.dto.DishReviewResponse;
import com.ljyh.foodieconnect.dto.DishReviewStatsResponse;
import com.ljyh.foodieconnect.entity.DishReview;
import com.ljyh.foodieconnect.entity.DishReviewImage;
import com.ljyh.foodieconnect.entity.MenuItem;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.DishReviewImageMapper;
import com.ljyh.foodieconnect.mapper.DishReviewMapper;
import com.ljyh.foodieconnect.mapper.MenuItemMapper;
import com.ljyh.foodieconnect.mapper.UserMapper;
import com.ljyh.foodieconnect.service.impl.DishReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 菜品评价服务测试类
 */
class DishReviewServiceTest {

    @Mock
    private DishReviewMapper dishReviewMapper;

    @Mock
    private DishReviewImageMapper dishReviewImageMapper;

    @Mock
    private MenuItemMapper menuItemMapper;

    @Mock
    private MenuItemService menuItemService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DishReviewServiceImpl dishReviewService;

    private MenuItem testMenuItem;
    private User testUser;
    private DishReview testReview;
    private DishReviewRequest testRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化测试数据 - 菜品
        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setRestaurantId(1L);
        testMenuItem.setName("宫保鸡丁");
        testMenuItem.setPrice(new BigDecimal("38.00"));
        testMenuItem.setImageUrl("/uploads/dish1.jpg");

        // 初始化测试数据 - 用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setDisplayName("测试用户");
        testUser.setAvatarUrl("/avatar/test.jpg");

        // 初始化测试数据 - 评价
        testReview = new DishReview();
        testReview.setId(1L);
        testReview.setMenuItemId(1L);
        testReview.setRestaurantId(1L);
        testReview.setUserId(1L);
        testReview.setRating(5);
        testReview.setComment("很好吃！");
        testReview.setCreatedAt(LocalDateTime.now());

        // 初始化测试数据 - 请求
        testRequest = new DishReviewRequest();
        testRequest.setRating(5);
        testRequest.setComment("很好吃！");
        testRequest.setImages(Arrays.asList("/uploads/review1.jpg", "/uploads/review2.jpg"));
    }

    @Test
    void testCreateReviewSuccess() {
        // 模拟依赖调用
        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewMapper.existsByMenuItemIdAndUserId(1L, 1L)).thenReturn(false);
        when(dishReviewMapper.insert(any(DishReview.class))).thenAnswer(invocation -> {
            DishReview review = invocation.getArgument(0);
            review.setId(1L);
            return 1;
        });
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(dishReviewMapper.selectById(1L)).thenReturn(testReview);
        doNothing().when(menuItemService).updateMenuItemRating(1L);

        // 执行测试
        DishReviewResponse response = dishReviewService.createReview(1L, testRequest, 1L);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(5, response.getRating());
        assertEquals("很好吃！", response.getComment());

        // 验证调用次数
        verify(dishReviewMapper, times(1)).insert(any(DishReview.class));
        verify(dishReviewImageMapper, times(2)).insert(any(DishReviewImage.class));
        verify(menuItemService, times(1)).updateMenuItemRating(1L);
    }

    @Test
    void testCreateReviewMenuItemNotFound() {
        // 模拟菜品不存在
        when(menuItemMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.createReview(999L, testRequest, 1L));
        assertEquals("MENU_ITEM_NOT_FOUND", exception.getCode());
        assertEquals("菜品不存在", exception.getMessage());
    }

    @Test
    void testCreateReviewAlreadyExists() {
        // 模拟已评价
        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewMapper.existsByMenuItemIdAndUserId(1L, 1L)).thenReturn(true);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.createReview(1L, testRequest, 1L));
        assertEquals("DISH_REVIEW_EXISTS", exception.getCode());
        assertEquals("您已对该菜品发表过评价", exception.getMessage());
    }

    @Test
    void testCreateReviewInvalidRatingTooLow() {
        // 设置无效评分
        testRequest.setRating(0);

        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewMapper.existsByMenuItemIdAndUserId(1L, 1L)).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.createReview(1L, testRequest, 1L));
        assertEquals("INVALID_RATING", exception.getCode());
        assertEquals("评分必须在1-5之间", exception.getMessage());
    }

    @Test
    void testCreateReviewInvalidRatingTooHigh() {
        // 设置无效评分
        testRequest.setRating(6);

        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewMapper.existsByMenuItemIdAndUserId(1L, 1L)).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.createReview(1L, testRequest, 1L));
        assertEquals("INVALID_RATING", exception.getCode());
        assertEquals("评分必须在1-5之间", exception.getMessage());
    }

    @Test
    void testGetMenuItemReviewsSuccess() {
        // 准备测试数据
        Page<DishReview> page = new Page<>(0, 10, 1);
        page.setRecords(Arrays.asList(testReview));

        // 模拟依赖调用
        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewMapper.findByMenuItemId(any(Page.class), eq(1L))).thenReturn(page);
        when(dishReviewMapper.selectById(1L)).thenReturn(testReview);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(dishReviewImageMapper.selectByDishReviewId(1L)).thenReturn(Arrays.asList());

        // 执行测试
        Page<DishReviewResponse> response = dishReviewService.getMenuItemReviews(1L, 0, 10, "latest");

        // 验证结果
        assertNotNull(response);
        assertEquals(1, response.getRecords().size());
        assertEquals(1L, response.getRecords().get(0).getId());
    }

    @Test
    void testGetMenuItemReviewsMenuItemNotFound() {
        // 模拟菜品不存在
        when(menuItemMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.getMenuItemReviews(999L, 0, 10, "latest"));
        assertEquals("MENU_ITEM_NOT_FOUND", exception.getCode());
    }

    @Test
    void testUpdateReviewSuccess() {
        // 准备更新数据
        DishReviewRequest updateRequest = new DishReviewRequest();
        updateRequest.setRating(4);
        updateRequest.setComment("还不错");
        updateRequest.setImages(Arrays.asList("/uploads/new1.jpg"));

        // 模拟依赖调用
        when(dishReviewMapper.selectById(1L)).thenReturn(testReview);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(dishReviewImageMapper.selectByDishReviewId(1L)).thenReturn(Arrays.asList());
        doNothing().when(menuItemService).updateMenuItemRating(1L);

        // 执行测试
        DishReviewResponse response = dishReviewService.updateReview(1L, updateRequest, 1L);

        // 验证结果
        assertNotNull(response);
        verify(dishReviewMapper, times(1)).updateById(any(DishReview.class));
        verify(menuItemService, times(1)).updateMenuItemRating(1L);
    }

    @Test
    void testUpdateReviewNotFound() {
        // 模拟评价不存在
        when(dishReviewMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.updateReview(999L, testRequest, 1L));
        assertEquals("REVIEW_NOT_FOUND", exception.getCode());
    }

    @Test
    void testUpdateReviewAccessDenied() {
        // 准备另一个用户的评价
        DishReview otherReview = new DishReview();
        otherReview.setId(2L);
        otherReview.setUserId(999L); // 不同用户

        when(dishReviewMapper.selectById(2L)).thenReturn(otherReview);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.updateReview(2L, testRequest, 1L));
        assertEquals("ACCESS_DENIED", exception.getCode());
        assertEquals("无权修改此评价", exception.getMessage());
    }

    @Test
    void testDeleteReviewSuccess() {
        // 模拟依赖调用
        when(dishReviewMapper.selectById(1L)).thenReturn(testReview);
        doNothing().when(menuItemService).updateMenuItemRating(1L);

        // 执行测试
        dishReviewService.deleteReview(1L, 1L);

        // 验证调用
        verify(dishReviewMapper, times(1)).deleteById(1L);
        verify(menuItemService, times(1)).updateMenuItemRating(1L);
    }

    @Test
    void testDeleteReviewNotFound() {
        // 模拟评价不存在
        when(dishReviewMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.deleteReview(999L, 1L));
        assertEquals("REVIEW_NOT_FOUND", exception.getCode());
    }

    @Test
    void testDeleteReviewAccessDenied() {
        // 准备另一个用户的评价
        DishReview otherReview = new DishReview();
        otherReview.setId(2L);
        otherReview.setMenuItemId(1L);
        otherReview.setUserId(999L);

        when(dishReviewMapper.selectById(2L)).thenReturn(otherReview);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.deleteReview(2L, 1L));
        assertEquals("ACCESS_DENIED", exception.getCode());
        assertEquals("无权删除此评价", exception.getMessage());
    }

    @Test
    void testCheckUserReviewExists() {
        // 模拟已评价
        when(dishReviewMapper.findByMenuItemIdAndUserId(1L, 1L)).thenReturn(testReview);

        // 执行测试
        DishReview result = dishReviewService.checkUserReview(1L, 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testCheckUserReviewNotExists() {
        // 模拟未评价
        when(dishReviewMapper.findByMenuItemIdAndUserId(1L, 1L)).thenReturn(null);

        // 执行测试
        DishReview result = dishReviewService.checkUserReview(1L, 1L);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testGetReviewStatsSuccess() {
        // 准备评分分布数据
        List<DishReviewMapper.RatingDistribution> distributions = Arrays.asList(
                createRatingDistribution(5, 50),
                createRatingDistribution(4, 25),
                createRatingDistribution(3, 15),
                createRatingDistribution(2, 5),
                createRatingDistribution(1, 5)
        );

        // 模拟依赖调用
        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewMapper.calculateAverageRating(1L)).thenReturn(4.3);
        when(dishReviewMapper.countByMenuItemId(1L)).thenReturn(100);
        when(dishReviewMapper.getRatingDistribution(1L)).thenReturn(distributions);

        // 执行测试
        DishReviewStatsResponse stats = dishReviewService.getReviewStats(1L);

        // 验证结果
        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(4.3), stats.getAverageRating());
        assertEquals(100, stats.getTotalReviews());
        assertEquals(50, stats.getRatingDistribution().get(5));
        assertEquals(25, stats.getRatingDistribution().get(4));
    }

    @Test
    void testGetReviewStatsMenuItemNotFound() {
        // 模拟菜品不存在
        when(menuItemMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.getReviewStats(999L));
        assertEquals("MENU_ITEM_NOT_FOUND", exception.getCode());
    }

    @Test
    void testGetReviewStatsNoReviews() {
        // 模拟没有评价
        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewMapper.calculateAverageRating(1L)).thenReturn(null);
        when(dishReviewMapper.countByMenuItemId(1L)).thenReturn(0);
        when(dishReviewMapper.getRatingDistribution(1L)).thenReturn(Arrays.asList());

        // 执行测试
        DishReviewStatsResponse stats = dishReviewService.getReviewStats(1L);

        // 验证结果
        assertNotNull(stats);
        assertEquals(BigDecimal.ZERO, stats.getAverageRating());
        assertEquals(0, stats.getTotalReviews());
    }

    @Test
    void testGetReviewByIdSuccess() {
        // 模拟依赖调用
        when(dishReviewMapper.selectById(1L)).thenReturn(testReview);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(menuItemMapper.selectById(1L)).thenReturn(testMenuItem);
        when(dishReviewImageMapper.selectByDishReviewId(1L)).thenReturn(Arrays.asList());

        // 执行测试
        DishReviewResponse response = dishReviewService.getReviewById(1L);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void testGetReviewByIdNotFound() {
        // 模拟评价不存在
        when(dishReviewMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dishReviewService.getReviewById(999L));
        assertEquals("REVIEW_NOT_FOUND", exception.getCode());
    }

    @Test
    void testGetMerchantReviewOverviewSuccess() {
        // 准备统计数据
        List<DishReviewMapper.ItemReviewStats> itemStats = Arrays.asList(
                createItemReviewStats(1L, "宫保鸡丁", 4.8, 50),
                createItemReviewStats(2L, "鱼香肉丝", 4.5, 30),
                createItemReviewStats(3L, "水煮鱼", 2.3, 10)
        );

        // 模拟依赖调用
        when(dishReviewMapper.getItemReviewStats(1L)).thenReturn(itemStats);

        // 执行测试
        Map<String, Object> overview = dishReviewService.getMerchantReviewOverview(1L);

        // 验证结果
        assertNotNull(overview);
        assertEquals(90, overview.get("totalReviews"));
        assertTrue(((Double) overview.get("averageRating")) > 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topRatedItems = (List<Map<String, Object>>) overview.get("topRatedItems");
        assertNotNull(topRatedItems);
        assertTrue(topRatedItems.size() > 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lowRatedItems = (List<Map<String, Object>>) overview.get("lowRatedItems");
        assertNotNull(lowRatedItems);
        assertTrue(lowRatedItems.size() > 0);
    }

    @Test
    void testGetMerchantReviewOverviewNoReviews() {
        // 模拟没有评价
        when(dishReviewMapper.getItemReviewStats(1L)).thenReturn(Arrays.asList());

        // 执行测试
        Map<String, Object> overview = dishReviewService.getMerchantReviewOverview(1L);

        // 验证结果
        assertNotNull(overview);
        assertEquals(0, overview.get("totalReviews"));
        assertEquals(0.0, overview.get("averageRating"));
    }

    // 辅助方法
    private DishReviewMapper.RatingDistribution createRatingDistribution(int rating, int count) {
        DishReviewMapper.RatingDistribution rd = new DishReviewMapper.RatingDistribution();
        rd.setRating(rating);
        rd.setCount(count);
        return rd;
    }

    private DishReviewMapper.ItemReviewStats createItemReviewStats(Long id, String name, Double rating, int count) {
        DishReviewMapper.ItemReviewStats stats = new DishReviewMapper.ItemReviewStats();
        stats.setMenuItemId(id);
        stats.setItemName(name);
        stats.setAverageRating(rating);
        stats.setReviewCount(count);
        return stats;
    }
}
