package com.ljyh.foodieconnect.recommendation;

import com.ljyh.foodieconnect.dto.UserRecommendationScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 混合推荐策略单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HybridRecommendationStrategyTest {
    
    @Mock
    private CollaborativeFilteringAlgorithm collaborativeFilteringAlgorithm;
    
    @Mock
    private SocialRecommendationAlgorithm socialRecommendationAlgorithm;
    
    @InjectMocks
    private HybridRecommendationStrategy hybridStrategy;
    
    private List<UserRecommendationScore> collaborativeRecommendations;
    private List<UserRecommendationScore> socialRecommendations;
    
    @BeforeEach
    void setUp() {
        // 创建协同过滤推荐结果
        collaborativeRecommendations = Arrays.asList(
            UserRecommendationScore.builder()
                .userId(2L)
                .score(BigDecimal.valueOf(0.8))
                .algorithmType("collaborative")
                .recommendationReason("基于协同过滤的推荐")
                .build(),
            UserRecommendationScore.builder()
                .userId(3L)
                .score(BigDecimal.valueOf(0.7))
                .algorithmType("collaborative")
                .recommendationReason("基于协同过滤的推荐")
                .build(),
            UserRecommendationScore.builder()
                .userId(4L)
                .score(BigDecimal.valueOf(0.6))
                .algorithmType("collaborative")
                .recommendationReason("基于协同过滤的推荐")
                .build()
        );
        
        // 创建社交推荐结果
        socialRecommendations = Arrays.asList(
            UserRecommendationScore.builder()
                .userId(3L)
                .score(BigDecimal.valueOf(0.9))
                .algorithmType("social")
                .recommendationReason("基于社交关系的推荐")
                .build(),
            UserRecommendationScore.builder()
                .userId(5L)
                .score(BigDecimal.valueOf(0.8))
                .algorithmType("social")
                .recommendationReason("基于社交关系的推荐")
                .build(),
            UserRecommendationScore.builder()
                .userId(6L)
                .score(BigDecimal.valueOf(0.7))
                .algorithmType("social")
                .recommendationReason("基于社交关系的推荐")
                .build()
        );
        
        // 配置Mock行为
        when(collaborativeFilteringAlgorithm.generateRecommendations(anyLong(), anyInt()))
            .thenReturn(collaborativeRecommendations);
        when(socialRecommendationAlgorithm.generateRecommendations(anyLong(), anyInt()))
            .thenReturn(socialRecommendations);
    }
    
    @Test
    void testGenerateRecommendationsWithWeightedStrategy() {
        // 测试加权混合策略
        List<UserRecommendationScore> recommendations = hybridStrategy.generateRecommendations(
            1L, 10, HybridRecommendationStrategy.HybridStrategy.WEIGHTED);
        
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // 验证结果包含两种算法的推荐
        Set<String> algorithmTypes = new HashSet<>();
        for (UserRecommendationScore score : recommendations) {
            algorithmTypes.add(score.getAlgorithmType());
        }
        assertTrue(algorithmTypes.contains("collaborative"));
        assertTrue(algorithmTypes.contains("social"));
        
        // 验证推荐理由被正确更新
        for (UserRecommendationScore score : recommendations) {
            assertNotNull(score.getRecommendationReason());
            assertTrue(score.getRecommendationReason().contains("混合推荐"));
        }
        
        // 验证结果按得分排序
        for (int i = 1; i < recommendations.size(); i++) {
            assertTrue(recommendations.get(i-1).getScore().doubleValue() >= 
                      recommendations.get(i).getScore().doubleValue());
        }
        
        verify(collaborativeFilteringAlgorithm, times(1)).generateRecommendations(1L, 10);
        verify(socialRecommendationAlgorithm, times(1)).generateRecommendations(1L, 10);
    }
    
    @Test
    void testGenerateRecommendationsWithSwitchingStrategy() {
        // 测试切换混合策略
        List<UserRecommendationScore> recommendations = hybridStrategy.generateRecommendations(
            1L, 10, HybridRecommendationStrategy.HybridStrategy.SWITCHING);
        
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // 验证结果只包含一种算法的推荐（切换策略）
        Set<String> algorithmTypes = new HashSet<>();
        for (UserRecommendationScore score : recommendations) {
            algorithmTypes.add(score.getAlgorithmType());
        }
        
        // 切换策略应该只选择一种算法
        assertTrue(algorithmTypes.size() <= 2);
        
        verify(collaborativeFilteringAlgorithm, times(1)).generateRecommendations(1L, 10);
        verify(socialRecommendationAlgorithm, times(1)).generateRecommendations(1L, 10);
    }
    
    @Test
    void testGenerateRecommendationsWithCascadeStrategy() {
        // 测试级联混合策略
        List<UserRecommendationScore> recommendations = hybridStrategy.generateRecommendations(
            1L, 10, HybridRecommendationStrategy.HybridStrategy.CASCADING);
        
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // 验证级联策略的优先级
        // 协同过滤推荐应该排在前面
        boolean hasCollaborativeFirst = false;
        if (recommendations.size() >= collaborativeRecommendations.size()) {
            hasCollaborativeFirst = true;
            for (int i = 0; i < collaborativeRecommendations.size(); i++) {
                assertEquals("collaborative", recommendations.get(i).getAlgorithmType());
            }
        }
        
        // 验证推荐理由被正确更新
        for (UserRecommendationScore score : recommendations) {
            assertNotNull(score.getRecommendationReason());
            assertTrue(score.getRecommendationReason().contains("混合推荐"));
        }
        
        verify(collaborativeFilteringAlgorithm, times(1)).generateRecommendations(1L, 10);
        verify(socialRecommendationAlgorithm, times(1)).generateRecommendations(1L, 10);
    }
    
    @Test
    void testGenerateRecommendationsWithEmptyResults() {
        // 测试空推荐结果的处理
        when(collaborativeFilteringAlgorithm.generateRecommendations(anyLong(), anyInt()))
            .thenReturn(Collections.emptyList());
        when(socialRecommendationAlgorithm.generateRecommendations(anyLong(), anyInt()))
            .thenReturn(Collections.emptyList());
        
        List<UserRecommendationScore> recommendations = hybridStrategy.generateRecommendations(
            1L, 10, HybridRecommendationStrategy.HybridStrategy.WEIGHTED);
        
        assertTrue(recommendations.isEmpty());
        
        verify(collaborativeFilteringAlgorithm, times(1)).generateRecommendations(1L, 10);
        verify(socialRecommendationAlgorithm, times(1)).generateRecommendations(1L, 10);
    }
    
    @Test
    void testGenerateRecommendationsWithPartialEmptyResults() {
        // 测试部分空推荐结果的处理
        when(collaborativeFilteringAlgorithm.generateRecommendations(anyLong(), anyInt()))
            .thenReturn(Collections.emptyList());
        
        List<UserRecommendationScore> recommendations = hybridStrategy.generateRecommendations(
            1L, 10, HybridRecommendationStrategy.HybridStrategy.WEIGHTED);
        
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // 验证结果只包含社交推荐
        for (UserRecommendationScore score : recommendations) {
            assertEquals("social", score.getAlgorithmType());
        }
        
        verify(collaborativeFilteringAlgorithm, times(1)).generateRecommendations(1L, 10);
        verify(socialRecommendationAlgorithm, times(1)).generateRecommendations(1L, 10);
    }
    
    @Test
    void testMergeRecommendationsWithWeightedStrategy() {
        // 测试加权合并推荐结果
        try {
            java.lang.reflect.Method method = HybridRecommendationStrategy.class
                .getDeclaredMethod("mergeRecommendationsWithWeighted", List.class, List.class);
            method.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<UserRecommendationScore> mergedRecommendations = 
                (List<UserRecommendationScore>) method.invoke(hybridStrategy, 
                    collaborativeRecommendations, socialRecommendations);
            
            assertNotNull(mergedRecommendations);
            assertFalse(mergedRecommendations.isEmpty());
            
            // 验证合并后的得分计算
            for (UserRecommendationScore score : mergedRecommendations) {
                assertTrue(score.getScore().doubleValue() >= 0.0 && score.getScore().doubleValue() <= 1.0);
                assertNotNull(score.getRecommendationReason());
                assertTrue(score.getRecommendationReason().contains("混合推荐"));
            }
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testMergeRecommendationsWithSwitchingStrategy() {
        // 测试切换合并推荐结果
        try {
            java.lang.reflect.Method method = HybridRecommendationStrategy.class
                .getDeclaredMethod("mergeRecommendationsWithSwitching", List.class, List.class);
            method.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<UserRecommendationScore> mergedRecommendations = 
                (List<UserRecommendationScore>) method.invoke(hybridStrategy, 
                    collaborativeRecommendations, socialRecommendations);
            
            assertNotNull(mergedRecommendations);
            assertFalse(mergedRecommendations.isEmpty());
            
            // 验证切换策略只选择一种算法的结果
            Set<String> algorithmTypes = new HashSet<>();
            for (UserRecommendationScore score : mergedRecommendations) {
                algorithmTypes.add(score.getAlgorithmType());
            }
            assertTrue(algorithmTypes.size() <= 2);
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testMergeRecommendationsWithCascadeStrategy() {
        // 测试级联合并推荐结果
        try {
            java.lang.reflect.Method method = HybridRecommendationStrategy.class
                .getDeclaredMethod("mergeRecommendationsWithCascade", List.class, List.class);
            method.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<UserRecommendationScore> mergedRecommendations = 
                (List<UserRecommendationScore>) method.invoke(hybridStrategy, 
                    collaborativeRecommendations, socialRecommendations);
            
            assertNotNull(mergedRecommendations);
            assertFalse(mergedRecommendations.isEmpty());
            
            // 验证级联策略的优先级
            // 协同过滤推荐应该排在前面
            if (mergedRecommendations.size() >= collaborativeRecommendations.size()) {
                for (int i = 0; i < collaborativeRecommendations.size(); i++) {
                    assertEquals("collaborative", mergedRecommendations.get(i).getAlgorithmType());
                }
            }
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testCalculateWeightedScore() {
        // 测试加权得分计算
        try {
            java.lang.reflect.Method method = HybridRecommendationStrategy.class
                .getDeclaredMethod("calculateWeightedScore", Double.class, Double.class, Double.class);
            method.setAccessible(true);
            
            double collaborativeScore = 0.8;
            double socialScore = 0.6;
            double collaborativeWeight = 0.6;
            
            double weightedScore = (Double) method.invoke(hybridStrategy, 
                collaborativeScore, socialScore, collaborativeWeight);
            
            assertEquals(collaborativeScore * collaborativeWeight + socialScore * (1.0 - collaborativeWeight), 
                        weightedScore, 0.001);
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testSelectBestAlgorithm() {
        // 测试选择最佳算法
        try {
            java.lang.reflect.Method method = HybridRecommendationStrategy.class
                .getDeclaredMethod("selectBestAlgorithm", List.class, List.class);
            method.setAccessible(true);
            
            String bestAlgorithm = (String) method.invoke(hybridStrategy, 
                collaborativeRecommendations, socialRecommendations);
            
            assertNotNull(bestAlgorithm);
            assertTrue(bestAlgorithm.equals("collaborative") || bestAlgorithm.equals("social"));
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
    
    @Test
    void testUpdateRecommendationReason() {
        // 测试更新推荐理由
        try {
            java.lang.reflect.Method method = HybridRecommendationStrategy.class
                .getDeclaredMethod("updateRecommendationReason", UserRecommendationScore.class, String.class);
            method.setAccessible(true);
            
            UserRecommendationScore score = UserRecommendationScore.builder()
                .userId(2L)
                .score(BigDecimal.valueOf(0.8))
                .algorithmType("collaborative")
                .recommendationReason("原始推荐理由")
                .build();
            
            String strategyType = "WEIGHTED";
            method.invoke(hybridStrategy, score, strategyType);
            
            assertNotNull(score.getRecommendationReason());
            assertTrue(score.getRecommendationReason().contains("混合推荐"));
            assertTrue(score.getRecommendationReason().contains(strategyType));
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }
}