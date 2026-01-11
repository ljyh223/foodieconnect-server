package com.ljyh.foodieconnect.recommendation;

import com.ljyh.foodieconnect.dto.UserRecommendationScore;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.entity.UserRestaurantVisit;
import com.ljyh.foodieconnect.entity.UserFollow;
import com.ljyh.foodieconnect.mapper.UserFollowMapper;
import com.ljyh.foodieconnect.mapper.UserMapper;
import com.ljyh.foodieconnect.mapper.UserRestaurantVisitMapper;
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
 * 社交推荐算法单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SocialRecommendationAlgorithmTest {
    
    @Mock
    private UserFollowMapper userFollowMapper;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private UserRestaurantVisitMapper userRestaurantVisitMapper;
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private org.springframework.data.redis.core.ValueOperations<String, Object> valueOperations;
    
    @InjectMocks
    private SocialRecommendationAlgorithm algorithm;
    
    private User testUser;
    private List<UserFollow> testFollows;
    private List<UserRestaurantVisit> testVisits;
    
    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setDisplayName("测试用户");
        testUser.setAvatarUrl("http://example.com/avatar.jpg");
        
        // 创建测试关注关系
        testFollows = Arrays.asList(
            createMockFollow(1L, 2L),
            createMockFollow(1L, 3L)
        );
        
        // 创建测试访问记录
        testVisits = Arrays.asList(
            createMockVisit(2L, 1L, UserRestaurantVisit.VisitType.REVIEW, 4.5),
            createMockVisit(2L, 2L, UserRestaurantVisit.VisitType.RECOMMENDATION, 4.0),
            createMockVisit(3L, 1L, UserRestaurantVisit.VisitType.FAVORITE, 5.0),
            createMockVisit(3L, 3L, UserRestaurantVisit.VisitType.REVIEW, 3.5)
        );
        
        // 配置Redis模板Mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }
    
    @Test
    void testGenerateRecommendationsWithEmptyFollows() {
        // 测试无关注关系的推荐生成
        when(userFollowMapper.getFollowingIds(1L)).thenReturn(Collections.emptyList());
        when(valueOperations.get(anyString())).thenReturn(null);
        
        List<UserRecommendationScore> recommendations = algorithm.generateRecommendations(1L, 10);
        
        assertTrue(recommendations.isEmpty());
        verify(userFollowMapper, times(1)).getFollowingIds(1L);
    }
    
    @Test
    void testGenerateRecommendationsWithValidData() {
        // 测试有效数据的推荐生成
        when(userFollowMapper.getFollowingIds(1L)).thenReturn(Arrays.asList(2L, 3L));
        when(userFollowMapper.getFollowersCount(anyLong())).thenReturn(5);
        when(userFollowMapper.getFollowingCount(anyLong())).thenReturn(3);
        when(userFollowMapper.findMutualFollowing(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(userRestaurantVisitMapper.findByUserId(anyLong())).thenReturn(testVisits);
        when(userRestaurantVisitMapper.findCommonVisitedRestaurants(anyLong(), anyLong())).thenReturn(testVisits);
        when(userMapper.selectById(anyLong())).thenReturn(testUser);
        when(valueOperations.get(anyString())).thenReturn(null);
        
        List<UserRecommendationScore> recommendations = algorithm.generateRecommendations(1L, 10);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= 10);
        
        // 验证推荐结果的基本属性
        for (UserRecommendationScore score : recommendations) {
            assertNotNull(score.getUserId());
            assertNotNull(score.getScore());
            assertEquals("social", score.getAlgorithmType());
            assertNotNull(score.getRecommendationReason());
            assertTrue(score.getScore().doubleValue() >= 0.0 && score.getScore().doubleValue() <= 1.0);
        }
        
        verify(userFollowMapper, times(1)).getFollowingIds(1L);
    }
    
    @Test
    void testGenerateRecommendationsFromCache() {
        // 测试从缓存获取推荐结果
        List<UserRecommendationScore> cachedRecommendations = Arrays.asList(
            UserRecommendationScore.builder()
                .userId(2L)
                .score(BigDecimal.valueOf(0.8))
                .algorithmType("social")
                .build()
        );
        
        when(valueOperations.get(anyString())).thenReturn(cachedRecommendations);
        
        List<UserRecommendationScore> recommendations = algorithm.generateRecommendations(1L, 10);
        
        assertEquals(cachedRecommendations, recommendations);
        verify(valueOperations, times(1)).get(anyString());
        verify(userFollowMapper, never()).getFollowingIds(anyLong());
    }
    
    @Test
    void testCalculateFirstDegreeScore() {
        // 测试一度关系得分计算
        try {
            java.lang.reflect.Method method = SocialRecommendationAlgorithm.class
                .getDeclaredMethod("calculateFirstDegreeScore", Long.class, Long.class);
            method.setAccessible(true);
            
            when(userFollowMapper.findMutualFollowing(anyLong(), anyLong())).thenReturn(new ArrayList<>());
            when(userFollowMapper.getFollowersCount(anyLong())).thenReturn(10);
            when(userFollowMapper.getFollowingCount(anyLong())).thenReturn(8);
            when(userRestaurantVisitMapper.findCommonVisitedRestaurants(anyLong(), anyLong()))
                .thenReturn(Arrays.asList(createMockVisit(1L, 1L, UserRestaurantVisit.VisitType.REVIEW, 4.0)));
            
            double score = (Double) method.invoke(algorithm, 1L, 2L);
            
            assertTrue(score > 0.0 && score <= 1.0);
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testCalculateSecondDegreeScore() {
        // 测试二度关系得分计算
        try {
            java.lang.reflect.Method method = SocialRecommendationAlgorithm.class
                .getDeclaredMethod("calculateSecondDegreeScore", Long.class, Long.class, Set.class);
            method.setAccessible(true);
            
            Set<Long> firstDegreeConnections = new HashSet<>(Arrays.asList(2L, 3L));
            when(userFollowMapper.findMutualFollowing(anyLong(), anyLong())).thenReturn(new ArrayList<>());
            when(userFollowMapper.getFollowersCount(anyLong())).thenReturn(8);
            when(userFollowMapper.getFollowingCount(anyLong())).thenReturn(6);
            when(userRestaurantVisitMapper.findCommonVisitedRestaurants(anyLong(), anyLong()))
                .thenReturn(Arrays.asList(createMockVisit(1L, 1L, UserRestaurantVisit.VisitType.REVIEW, 4.0)));
            
            double score = (Double) method.invoke(algorithm, 1L, 4L, firstDegreeConnections);
            
            assertTrue(score > 0.0 && score <= 1.0);
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testCalculateCommonInterestsScore() {
        // 测试共同兴趣得分计算
        try {
            java.lang.reflect.Method method = SocialRecommendationAlgorithm.class
                .getDeclaredMethod("calculateCommonInterestsScore", Long.class, Long.class);
            method.setAccessible(true);
            
            when(userRestaurantVisitMapper.findCommonVisitedRestaurants(anyLong(), anyLong()))
                .thenReturn(Arrays.asList(
                    createMockVisit(1L, 1L, UserRestaurantVisit.VisitType.REVIEW, 4.5),
                    createMockVisit(1L, 2L, UserRestaurantVisit.VisitType.FAVORITE, 5.0)
                ));
            
            double score = (Double) method.invoke(algorithm, 1L, 2L);
            
            assertTrue(score >= 0.0 && score <= 1.0);
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testCalculateInfluenceScore() {
        // 测试影响力得分计算
        try {
            java.lang.reflect.Method method = SocialRecommendationAlgorithm.class
                .getDeclaredMethod("calculateInfluenceScore", Long.class);
            method.setAccessible(true);
            
            when(userFollowMapper.getFollowersCount(anyLong())).thenReturn(15);
            when(userFollowMapper.getFollowingCount(anyLong())).thenReturn(5);
            when(userRestaurantVisitMapper.getVisitedRestaurantsCount(anyLong())).thenReturn(10);
            
            double score = (Double) method.invoke(algorithm, 2L);
            
            assertTrue(score >= 0.0 && score <= 1.0);
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testGenerateRecommendationReason() {
        // 测试推荐理由生成
        try {
            java.lang.reflect.Method method = SocialRecommendationAlgorithm.class
                .getDeclaredMethod("generateRecommendationReason", Long.class, Long.class, 
                    Integer.class, Integer.class, Integer.class, Double.class);
            method.setAccessible(true);
            
            String reason = (String) method.invoke(algorithm, 1L, 2L, 3, 2, 5, 0.8);
            
            assertNotNull(reason);
            assertFalse(reason.isEmpty());
            assertTrue(reason.contains("共同关注") || reason.contains("共同访问") || reason.contains("社交关系"));
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testGetSecondDegreeConnections() {
        // 测试获取二度关系连接
        try {
            java.lang.reflect.Method method = SocialRecommendationAlgorithm.class
                .getDeclaredMethod("getSecondDegreeConnections", Long.class, Set.class);
            method.setAccessible(true);
            
            Set<Long> firstDegreeConnections = new HashSet<>(Arrays.asList(2L, 3L));
            
            when(userFollowMapper.getFollowingIds(2L)).thenReturn(Arrays.asList(4L, 5L));
            when(userFollowMapper.getFollowingIds(3L)).thenReturn(Arrays.asList(5L, 6L));
            
            @SuppressWarnings("unchecked")
            Set<Long> secondDegreeConnections = (Set<Long>) method.invoke(algorithm, 1L, firstDegreeConnections);
            
            assertNotNull(secondDegreeConnections);
            assertTrue(secondDegreeConnections.contains(4L));
            assertTrue(secondDegreeConnections.contains(5L));
            assertTrue(secondDegreeConnections.contains(6L));
            assertFalse(secondDegreeConnections.contains(1L)); // 不包含自己
            assertFalse(secondDegreeConnections.contains(2L)); // 不包含一度关系
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建模拟关注关系
     */
    private UserFollow createMockFollow(Long followerId, Long followingId) {
        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        return follow;
    }
    
    /**
     * 创建模拟访问记录
     */
    private UserRestaurantVisit createMockVisit(Long userId, Long restaurantId, 
                                           UserRestaurantVisit.VisitType visitType, double rating) {
        UserRestaurantVisit visit = new UserRestaurantVisit();
        visit.setUserId(userId);
        visit.setRestaurantId(restaurantId);
        visit.setVisitType(visitType);
        visit.setRating(BigDecimal.valueOf(rating));
        visit.setVisitCount(1);
        return visit;
    }
}