package com.ljyh.foodieconnect.service;

import com.ljyh.foodieconnect.dto.UserRecommendationScore;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.entity.UserRecommendation;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.UserRecommendationMapper;
import com.ljyh.foodieconnect.mapper.UserRecommendationMapper.UserRecommendationWithUserInfo;
import com.ljyh.foodieconnect.mapper.UserRecommendationMapper.AlgorithmStats;
import com.ljyh.foodieconnect.recommendation.HybridRecommendationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户推荐服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserRecommendationServiceTest {
    
    @Mock
    private HybridRecommendationStrategy hybridRecommendationStrategy;
    
    @Mock
    private UserRecommendationMapper userRecommendationMapper;
    
    @Mock
    private UserService userService;
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @InjectMocks
    private UserRecommendationService userRecommendationService;
    
    private List<UserRecommendationScore> testRecommendations;
    private User testUser;
    private UserRecommendation testRecommendation;
    
    @BeforeEach
    void setUp() {
        // 创建测试推荐结果
        testRecommendations = Arrays.asList(
            UserRecommendationScore.builder()
                .userId(2L)
                .score(BigDecimal.valueOf(0.8))
                .algorithmType("WEIGHTED")
                .recommendationReason("基于协同过滤和社交关系的混合推荐")
                .build(),
            UserRecommendationScore.builder()
                .userId(3L)
                .score(BigDecimal.valueOf(0.7))
                .algorithmType("WEIGHTED")
                .recommendationReason("基于协同过滤和社交关系的混合推荐")
                .build()
        );
        
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setDisplayName("测试用户");
        testUser.setEmail("test@example.com");
        
        // 创建测试推荐记录
        testRecommendation = new UserRecommendation();
        testRecommendation.setId(1L);
        testRecommendation.setUserId(1L);
        testRecommendation.setRecommendedUserId(2L);
        testRecommendation.setAlgorithmType("WEIGHTED");
        testRecommendation.setRecommendationScore(BigDecimal.valueOf(0.8));
        testRecommendation.setRecommendationReason("测试推荐理由");
        testRecommendation.setIsViewed(false);
        testRecommendation.setIsInterested(null);
    }
    
    @Test
    void testGetUserRecommendations() {
        // 测试获取用户推荐列表
        when(hybridRecommendationStrategy.generateRecommendations(anyLong(), anyInt(), any()))
            .thenReturn(testRecommendations);
        when(userRecommendationMapper.findByUserIdAndRecommendedUserIdAndAlgorithm(anyLong(), anyLong(), anyString()))
            .thenReturn(null);
        
        List<UserRecommendationScore> recommendations = 
            userRecommendationService.getUserRecommendations(1L, 10, "WEIGHTED");
        
        assertNotNull(recommendations);
        assertEquals(2, recommendations.size());
        
        // 验证推荐结果的基本属性
        for (UserRecommendationScore score : recommendations) {
            assertNotNull(score.getUserId());
            assertNotNull(score.getScore());
            assertNotNull(score.getAlgorithmType());
            assertNotNull(score.getRecommendationReason());
        }
        
        verify(hybridRecommendationStrategy, times(1))
            .generateRecommendations(1L, 10, HybridRecommendationStrategy.HybridStrategy.WEIGHTED);
    }
    
    @Test
    void testGetUserRecommendationsWithInvalidLimit() {
        // 测试无效限制数量的推荐获取
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.getUserRecommendations(1L, 0, "WEIGHTED");
        });
        
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.getUserRecommendations(1L, 51, "WEIGHTED");
        });
    }
    
    @Test
    void testGetUserRecommendationsWithUnknownAlgorithm() {
        // 测试未知算法类型的推荐获取
        when(hybridRecommendationStrategy.generateRecommendations(anyLong(), anyInt(), any()))
            .thenReturn(testRecommendations);
        when(userRecommendationMapper.findByUserIdAndRecommendedUserIdAndAlgorithm(anyLong(), anyLong(), anyString()))
            .thenReturn(null);
        
        List<UserRecommendationScore> recommendations = 
            userRecommendationService.getUserRecommendations(1L, 10, "UNKNOWN");
        
        assertNotNull(recommendations);
        verify(hybridRecommendationStrategy, times(1))
            .generateRecommendations(1L, 10, HybridRecommendationStrategy.HybridStrategy.WEIGHTED);
    }
    
    @Test
    void testMarkRecommendationStatus() {
        // 测试标记推荐状态
        when(userRecommendationMapper.selectById(1L)).thenReturn(testRecommendation);
        
        userRecommendationService.markRecommendationStatus(1L, 1L, true, "感兴趣");
        
        assertTrue(testRecommendation.getIsViewed());
        assertTrue(testRecommendation.getIsInterested());
        assertEquals("感兴趣", testRecommendation.getFeedback());
        
        verify(userRecommendationMapper, times(1)).updateById(testRecommendation);
    }
    
    @Test
    void testMarkRecommendationStatusWithNotFound() {
        // 测试标记不存在的推荐状态
        when(userRecommendationMapper.selectById(1L)).thenReturn(null);
        
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.markRecommendationStatus(1L, 1L, true, "感兴趣");
        });
    }
    
    @Test
    void testMarkRecommendationStatusWithPermissionDenied() {
        // 测试标记无权限的推荐状态
        testRecommendation.setUserId(2L); // 设置为其他用户的推荐
        when(userRecommendationMapper.selectById(1L)).thenReturn(testRecommendation);
        
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.markRecommendationStatus(1L, 1L, true, "感兴趣");
        });
    }
    
    @Test
    void testGetUserRecommendationStats() {
        // 测试获取用户推荐统计信息
        when(userRecommendationMapper.countByUserId(1L)).thenReturn(10);
        when(userRecommendationMapper.countByUserIdAndViewed(1L, true)).thenReturn(6);
        when(userRecommendationMapper.countByUserIdAndInterested(1L, true)).thenReturn(3);
        
        UserRecommendationService.RecommendationStats stats = 
            userRecommendationService.getUserRecommendationStats(1L);
        
        assertNotNull(stats);
        assertEquals(10, stats.getTotalRecommendations());
        assertEquals(6, stats.getViewedCount());
        assertEquals(3, stats.getInterestedCount());
        assertEquals(0.6, stats.getClickThroughRate(), 0.001);
        assertEquals(0.5, stats.getConversionRate(), 0.001);
        
        verify(userRecommendationMapper, times(1)).countByUserId(1L);
        verify(userRecommendationMapper, times(1)).countByUserIdAndViewed(1L, true);
        verify(userRecommendationMapper, times(1)).countByUserIdAndInterested(1L, true);
    }
    
    @Test
    void testCleanExpiredRecommendations() {
        // 测试清除过期推荐记录
        when(userRecommendationMapper.deleteByCreatedAtBefore(any())).thenReturn(5);
        
        userRecommendationService.cleanExpiredRecommendations();
        
        verify(userRecommendationMapper, times(1)).deleteByCreatedAtBefore(any());
    }
    
    @Test
    void testGetUserRecommendationsWithPagination() {
        // 测试获取用户推荐列表（分页）
        List<UserRecommendationWithUserInfo> mockRecommendations = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            UserRecommendationWithUserInfo recommendation = new UserRecommendationWithUserInfo();
            recommendation.setId((long) i);
            recommendation.setUserId(1L);
            recommendation.setRecommendedUserId((long) (i + 2));
            recommendation.setRecommendedUserName("用户" + (i + 2));
            recommendation.setRecommendedUserAvatar("http://example.com/avatar" + (i + 2) + ".jpg");
            mockRecommendations.add(recommendation);
        }
        
        when(userRecommendationMapper.findByUserIdWithPagination(1L, 0, 10))
            .thenReturn(mockRecommendations);
        
        List<UserRecommendationWithUserInfo> recommendations = 
            userRecommendationService.getUserRecommendationsWithPagination(1L, 0, 10);
        
        assertNotNull(recommendations);
        assertEquals(5, recommendations.size());
        
        verify(userRecommendationMapper, times(1)).findByUserIdWithPagination(1L, 0, 10);
    }
    
    @Test
    void testGetUserRecommendationsWithPaginationWithInvalidParams() {
        // 测试无效参数的分页推荐获取
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.getUserRecommendationsWithPagination(1L, -1, 10);
        });
        
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.getUserRecommendationsWithPagination(1L, 0, 0);
        });
        
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.getUserRecommendationsWithPagination(1L, 0, 21);
        });
    }
    
    @Test
    void testGetUnviewedRecommendations() {
        // 测试获取未查看的推荐列表
        List<UserRecommendationWithUserInfo> mockRecommendations = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UserRecommendationWithUserInfo recommendation = new UserRecommendationWithUserInfo();
            recommendation.setId((long) i);
            recommendation.setUserId(1L);
            recommendation.setRecommendedUserId((long) (i + 2));
            recommendation.setRecommendedUserName("用户" + (i + 2));
            recommendation.setRecommendedUserAvatar("http://example.com/avatar" + (i + 2) + ".jpg");
            mockRecommendations.add(recommendation);
        }
        
        when(userRecommendationMapper.findUnviewedByUserId(1L, 10))
            .thenReturn(mockRecommendations);
        
        List<UserRecommendationWithUserInfo> recommendations = 
            userRecommendationService.getUnviewedRecommendations(1L, 10);
        
        assertNotNull(recommendations);
        assertEquals(3, recommendations.size());
        
        verify(userRecommendationMapper, times(1)).findUnviewedByUserId(1L, 10);
    }
    
    @Test
    void testBatchMarkAsViewed() {
        // 测试批量标记为已查看
        List<Long> recommendationIds = Arrays.asList(1L, 2L, 3L);
        
        when(userRecommendationMapper.selectById(anyLong())).thenReturn(testRecommendation);
        when(userRecommendationMapper.batchUpdateViewedStatus(recommendationIds, true))
            .thenReturn(3);
        
        userRecommendationService.batchMarkAsViewed(1L, recommendationIds);
        
        verify(userRecommendationMapper, times(3)).selectById(anyLong());
        verify(userRecommendationMapper, times(1)).batchUpdateViewedStatus(recommendationIds, true);
    }
    
    @Test
    void testGetUserAlgorithmStats() {
        // 测试获取用户算法统计信息
        List<AlgorithmStats> mockStats = Arrays.asList(
            createMockAlgorithmStats("WEIGHTED", 10L, 0.8, 6L, 3L),
            createMockAlgorithmStats("SWITCHING", 5L, 0.7, 3L, 1L)
        );
        
        when(userRecommendationMapper.getAlgorithmStatsByUserId(1L)).thenReturn(mockStats);
        
        List<AlgorithmStats> stats = userRecommendationService.getUserAlgorithmStats(1L);
        
        assertNotNull(stats);
        assertEquals(2, stats.size());
        assertEquals("WEIGHTED", stats.get(0).getAlgorithmType());
        assertEquals(10L, stats.get(0).getTotalCount());
        
        verify(userRecommendationMapper, times(1)).getAlgorithmStatsByUserId(1L);
    }
    
    @Test
    void testGetGlobalAlgorithmStats() {
        // 测试获取全局算法统计信息
        List<AlgorithmStats> mockStats = Arrays.asList(
            createMockAlgorithmStats("WEIGHTED", 100L, 0.8, 60L, 30L),
            createMockAlgorithmStats("SWITCHING", 50L, 0.7, 30L, 10L)
        );
        
        when(userRecommendationMapper.getGlobalAlgorithmStats(7)).thenReturn(mockStats);
        
        List<AlgorithmStats> stats = userRecommendationService.getGlobalAlgorithmStats(7);
        
        assertNotNull(stats);
        assertEquals(2, stats.size());
        assertEquals("WEIGHTED", stats.get(0).getAlgorithmType());
        assertEquals(100L, stats.get(0).getTotalCount());
        
        verify(userRecommendationMapper, times(1)).getGlobalAlgorithmStats(7);
    }
    
    @Test
    void testGetGlobalAlgorithmStatsWithInvalidDays() {
        // 测试无效天数的全局算法统计获取
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.getGlobalAlgorithmStats(0);
        });
        
        assertThrows(BusinessException.class, () -> {
            userRecommendationService.getGlobalAlgorithmStats(366);
        });
    }
    
    @Test
    void testGetRecentlyRecommendedUserIds() {
        // 测试获取最近推荐的推荐用户ID列表
        List<Long> recommendedUserIds = Arrays.asList(2L, 3L, 4L);
        
        when(userRecommendationMapper.getRecommendedUserIds(1L, 30)).thenReturn(recommendedUserIds);
        
        List<Long> result = userRecommendationService.getRecentlyRecommendedUserIds(1L, 30);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
        assertTrue(result.contains(4L));
        
        verify(userRecommendationMapper, times(1)).getRecommendedUserIds(1L, 30);
    }
    
    @Test
    void testGetRecommendationDetail() {
        // 测试获取推荐详情
        UserRecommendationWithUserInfo mockDetail = new UserRecommendationWithUserInfo();
        mockDetail.setId(1L);
        mockDetail.setUserId(1L);
        mockDetail.setRecommendedUserId(2L);
        mockDetail.setRecommendedUserName("用户2");
        mockDetail.setRecommendedUserAvatar("http://example.com/avatar2.jpg");
        
        when(userRecommendationMapper.selectById(1L)).thenReturn(testRecommendation);
        when(userRecommendationMapper.findByUserIdWithPagination(1L, 0, 1))
            .thenReturn(Arrays.asList(mockDetail));
        
        UserRecommendationWithUserInfo detail = userRecommendationService.getRecommendationDetail(1L, 1L);
        
        assertNotNull(detail);
        assertEquals(1L, detail.getId());
        assertEquals(1L, detail.getUserId());
        assertEquals(2L, detail.getRecommendedUserId());
        assertEquals("用户2", detail.getRecommendedUserName());
        
        verify(userRecommendationMapper, times(1)).selectById(1L);
        verify(userRecommendationMapper, times(1)).findByUserIdWithPagination(1L, 0, 1);
    }
    
    @Test
    void testDeleteRecommendation() {
        // 测试删除推荐记录
        when(userRecommendationMapper.selectById(1L)).thenReturn(testRecommendation);
        
        userRecommendationService.deleteRecommendation(1L, 1L);
        
        verify(userRecommendationMapper, times(1)).deleteById(1L);
    }
    
    @Test
    void testClearAllUserRecommendations() {
        // 测试清除用户所有推荐记录
        when(userService.getUserById(1L)).thenReturn(new com.ljyh.foodieconnect.dto.UserDTO());
        when(userRecommendationMapper.delete(any())).thenReturn(5);
        
        userRecommendationService.clearAllUserRecommendations(1L);
        
        verify(userService, times(1)).getUserById(1L);
        verify(userRecommendationMapper, times(1)).delete(any());
    }
    
    @Test
    void testWarmupRecommendationCache() {
        // 测试预热推荐缓存
        when(userService.getUserById(1L)).thenReturn(new com.ljyh.foodieconnect.dto.UserDTO());
        when(hybridRecommendationStrategy.generateRecommendations(anyLong(), anyInt(), any()))
            .thenReturn(testRecommendations);
        
        userRecommendationService.warmupRecommendationCache(1L);
        
        verify(userService, times(1)).getUserById(1L);
        verify(hybridRecommendationStrategy, times(3))
            .generateRecommendations(eq(1L), eq(10), any());
    }
    
    /**
     * 创建模拟算法统计信息
     */
    private AlgorithmStats createMockAlgorithmStats(String algorithmType, Long totalCount, 
                                                     Double avgScore, Long viewedCount, Long interestedCount) {
        AlgorithmStats stats = new AlgorithmStats();
        stats.setAlgorithmType(algorithmType);
        stats.setTotalCount(totalCount);
        stats.setAvgScore(avgScore);
        stats.setViewedCount(viewedCount);
        stats.setInterestedCount(interestedCount);
        return stats;
    }
}