package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljyh.foodieconnect.dto.DishReviewRequest;
import com.ljyh.foodieconnect.dto.DishReviewResponse;
import com.ljyh.foodieconnect.dto.DishReviewStatsResponse;
import com.ljyh.foodieconnect.entity.DishReview;

import java.util.List;
import java.util.Map;

/**
 * 菜品评价服务接口
 */
public interface DishReviewService extends IService<DishReview> {

    /**
     * 创建菜品评价
     *
     * @param menuItemId 菜品ID
     * @param request 评价请求
     * @param userId 用户ID
     * @return 评价响应
     */
    DishReviewResponse createReview(Long menuItemId, DishReviewRequest request, Long userId);

    /**
     * 获取菜品评价列表
     *
     * @param menuItemId 菜品ID
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序方式
     * @return 分页评价列表
     */
    Page<DishReviewResponse> getMenuItemReviews(Long menuItemId, int page, int size, String sortBy);

    /**
     * 获取用户的菜品评价列表
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param page 页码
     * @param size 每页大小
     * @param menuItemId 菜品ID（可选）
     * @return 分页评价列表
     */
    Page<DishReviewResponse> getUserReviews(Long userId, Long restaurantId, int page, int size, Long menuItemId);

    /**
     * 更新菜品评价
     *
     * @param reviewId 评价ID
     * @param request 评价请求
     * @param userId 用户ID
     * @return 更新后的评价
     */
    DishReviewResponse updateReview(Long reviewId, DishReviewRequest request, Long userId);

    /**
     * 删除菜品评价
     *
     * @param reviewId 评价ID
     * @param userId 用户ID
     */
    void deleteReview(Long reviewId, Long userId);

    /**
     * 检查用户是否已评价该菜品
     *
     * @param menuItemId 菜品ID
     * @param userId 用户ID
     * @return 评价（如果存在）
     */
    DishReview checkUserReview(Long menuItemId, Long userId);

    /**
     * 获取菜品评分统计
     *
     * @param menuItemId 菜品ID
     * @return 评分统计
     */
    DishReviewStatsResponse getReviewStats(Long menuItemId);

    /**
     * 获取菜品评价详情
     *
     * @param reviewId 评价ID
     * @return 评价详情
     */
    DishReviewResponse getReviewById(Long reviewId);

    /**
     * 商家查看菜品评价列表
     *
     * @param restaurantId 餐厅ID
     * @param menuItemId 菜品ID
     * @param page 页码
     * @param size 每页大小
     * @param rating 评分筛选（可选）
     * @return 分页评价列表
     */
    Page<DishReviewResponse> getMerchantItemReviews(Long restaurantId, Long menuItemId, int page, int size, Integer rating);

    /**
     * 商家获取评价概览
     *
     * @param restaurantId 餐厅ID
     * @return 评价概览数据
     */
    Map<String, Object> getMerchantReviewOverview(Long restaurantId);
}
