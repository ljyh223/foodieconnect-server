package com.ljyh.foodieconnect.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.ljyh.foodieconnect.service.DishReviewService;
import com.ljyh.foodieconnect.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜品评价服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DishReviewServiceImpl extends ServiceImpl<DishReviewMapper, DishReview> implements DishReviewService {

    private final DishReviewMapper dishReviewMapper;
    private final DishReviewImageMapper dishReviewImageMapper;
    private final MenuItemMapper menuItemMapper;
    private final MenuItemService menuItemService;
    private final UserMapper userMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public DishReviewResponse createReview(Long menuItemId, DishReviewRequest request, Long userId) {
        // 验证菜品是否存在
        MenuItem menuItem = menuItemMapper.selectById(menuItemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }

        // 检查用户是否已评价过该菜品
        if (dishReviewMapper.existsByMenuItemIdAndUserId(menuItemId, userId)) {
            throw new BusinessException("DISH_REVIEW_EXISTS", "您已对该菜品发表过评价");
        }

        // 验证评分范围
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BusinessException("INVALID_RATING", "评分必须在1-5之间");
        }

        // 创建评价
        DishReview review = new DishReview();
        review.setMenuItemId(menuItemId);
        review.setRestaurantId(menuItem.getRestaurantId());
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        dishReviewMapper.insert(review);
        log.info("用户 {} 对菜品 {} 发表评价，评分: {}", userId, menuItemId, request.getRating());

        // 保存评价图片
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            saveReviewImages(review.getId(), request.getImages());
        }

        // 更新菜品评分
        menuItemService.updateMenuItemRating(menuItemId);

        // 查询并构建响应
        return buildReviewResponse(review);
    }

    @Override
    public Page<DishReviewResponse> getMenuItemReviews(Long menuItemId, int page, int size, String sortBy) {
        // 验证菜品是否存在
        MenuItem menuItem = menuItemMapper.selectById(menuItemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }

        Page<DishReview> pageParam = new Page<>(page, size);
        Page<DishReview> reviewPage = dishReviewMapper.findByMenuItemId(pageParam, menuItemId);

        // 转换为响应DTO
        Page<DishReviewResponse> responsePage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        List<DishReviewResponse> responses = reviewPage.getRecords().stream()
                .map(this::buildReviewResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responses);

        return responsePage;
    }

    @Override
    public Page<DishReviewResponse> getUserReviews(Long userId, Long restaurantId, int page, int size, Long menuItemId) {
        Page<DishReview> pageParam = new Page<>(page, size);
        Page<DishReview> reviewPage = dishReviewMapper.findByUserId(pageParam, userId);

        // 转换为响应DTO
        Page<DishReviewResponse> responsePage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        List<DishReviewResponse> responses = reviewPage.getRecords().stream()
                .map(this::buildReviewResponse)
                .collect(Collectors.toList());

        // 筛选指定餐厅的评价（restaurantId 现在是必需的）
        responses = responses.stream()
                .filter(r -> {
                    DishReview review = dishReviewMapper.selectById(r.getId());
                    // 验证餐厅ID必须匹配
                    if (!review.getRestaurantId().equals(restaurantId)) {
                        return false;
                    }
                    // 如果指定了菜品ID，则筛选该菜品
                    if (menuItemId != null && !review.getMenuItemId().equals(menuItemId)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        responsePage.setRecords(responses);
        return responsePage;
    }

    @Override
    @Transactional
    public DishReviewResponse updateReview(Long reviewId, DishReviewRequest request, Long userId) {
        DishReview review = dishReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("REVIEW_NOT_FOUND", "评价不存在");
        }

        // 检查权限
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("ACCESS_DENIED", "无权修改此评价");
        }

        // 验证评分范围
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BusinessException("INVALID_RATING", "评分必须在1-5之间");
        }

        // 更新评价
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        dishReviewMapper.updateById(review);
        log.info("用户 {} 更新菜品评价 {}", userId, reviewId);

        // 删除旧图片并保存新图片
        if (request.getImages() != null) {
            dishReviewImageMapper.deleteByDishReviewId(reviewId);
            if (!request.getImages().isEmpty()) {
                saveReviewImages(reviewId, request.getImages());
            }
        }

        // 更新菜品评分
        menuItemService.updateMenuItemRating(review.getMenuItemId());

        return buildReviewResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        DishReview review = dishReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("REVIEW_NOT_FOUND", "评价不存在");
        }

        // 检查权限
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("ACCESS_DENIED", "无权删除此评价");
        }

        Long menuItemId = review.getMenuItemId();
        dishReviewMapper.deleteById(reviewId);
        log.info("用户 {} 删除菜品评价 {}", userId, reviewId);

        // 更新菜品评分
        menuItemService.updateMenuItemRating(menuItemId);
    }

    @Override
    public DishReview checkUserReview(Long menuItemId, Long userId) {
        return dishReviewMapper.findByMenuItemIdAndUserId(menuItemId, userId);
    }

    @Override
    public DishReviewStatsResponse getReviewStats(Long menuItemId) {
        // 验证菜品是否存在
        MenuItem menuItem = menuItemMapper.selectById(menuItemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }

        Double averageRating = dishReviewMapper.calculateAverageRating(menuItemId);
        Integer totalReviews = dishReviewMapper.countByMenuItemId(menuItemId);
        List<DishReviewMapper.RatingDistribution> distribution = dishReviewMapper.getRatingDistribution(menuItemId);

        Map<Integer, Integer> ratingMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingMap.put(i, 0);
        }
        for (DishReviewMapper.RatingDistribution rd : distribution) {
            ratingMap.put(rd.getRating(), rd.getCount());
        }

        DishReviewStatsResponse response = new DishReviewStatsResponse();
        response.setAverageRating(averageRating != null ? java.math.BigDecimal.valueOf(averageRating) : java.math.BigDecimal.ZERO);
        response.setTotalReviews(totalReviews != null ? totalReviews : 0);
        response.setRatingDistribution(ratingMap);

        return response;
    }

    @Override
    public DishReviewResponse getReviewById(Long reviewId) {
        DishReview review = dishReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("REVIEW_NOT_FOUND", "评价不存在");
        }
        return buildReviewResponse(review);
    }

    @Override
    public Page<DishReviewResponse> getMerchantItemReviews(Long restaurantId, Long menuItemId, int page, int size, Integer rating) {
        Page<DishReview> pageParam = new Page<>(page, size);
        Page<DishReview> reviewPage = dishReviewMapper.findByRestaurantIdAndItemId(pageParam, restaurantId, menuItemId, rating);

        // 转换为响应DTO
        Page<DishReviewResponse> responsePage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        List<DishReviewResponse> responses = reviewPage.getRecords().stream()
                .map(this::buildReviewResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responses);

        return responsePage;
    }

    @Override
    public Map<String, Object> getMerchantReviewOverview(Long restaurantId) {
        List<DishReviewMapper.ItemReviewStats> itemStats = dishReviewMapper.getItemReviewStats(restaurantId);

        // 计算总体统计
        int totalReviews = 0;
        double totalRating = 0.0;

        List<Map<String, Object>> topRatedItems = new ArrayList<>();
        List<Map<String, Object>> lowRatedItems = new ArrayList<>();

        for (DishReviewMapper.ItemReviewStats stat : itemStats) {
            if (stat.getReviewCount() > 0) {
                totalReviews += stat.getReviewCount();
                totalRating += stat.getAverageRating() * stat.getReviewCount();

                Map<String, Object> itemData = new HashMap<>();
                itemData.put("menuItemId", stat.getMenuItemId());
                itemData.put("itemName", stat.getItemName());
                itemData.put("averageRating", stat.getAverageRating());
                itemData.put("reviewCount", stat.getReviewCount());

                if (stat.getReviewCount() >= 3) {
                    if (stat.getAverageRating() >= 4.0) {
                        topRatedItems.add(itemData);
                    } else if (stat.getAverageRating() <= 2.5) {
                        lowRatedItems.add(itemData);
                    }
                }
            }
        }

        // 限制返回数量
        topRatedItems = topRatedItems.stream().limit(5).collect(Collectors.toList());
        lowRatedItems = lowRatedItems.stream().limit(5).collect(Collectors.toList());

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalReviews", totalReviews);
        overview.put("averageRating", totalReviews > 0 ? totalRating / totalReviews : 0.0);
        overview.put("topRatedItems", topRatedItems);
        overview.put("lowRatedItems", lowRatedItems);

        return overview;
    }

    /**
     * 保存评价图片
     */
    private void saveReviewImages(Long reviewId, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            DishReviewImage reviewImage = new DishReviewImage();
            reviewImage.setDishReviewId(reviewId);
            reviewImage.setImageUrl(imageUrls.get(i));
            reviewImage.setSortOrder(i + 1);
            dishReviewImageMapper.insert(reviewImage);
        }
        log.info("为评价 {} 保存了 {} 张图片", reviewId, imageUrls.size());
    }

    /**
     * 构建评价响应DTO
     */
    private DishReviewResponse buildReviewResponse(DishReview review) {
        // 重新查询以获取完整数据
        review = dishReviewMapper.selectById(review.getId());

        DishReviewResponse response = new DishReviewResponse();
        response.setId(review.getId());
        response.setMenuItemId(review.getMenuItemId());
        response.setUserId(review.getUserId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());

        // 设置时间
        if (review.getCreatedAt() != null) {
            response.setCreatedAt(review.getCreatedAt().format(DATE_FORMATTER));
        }

        // 获取用户信息
        User user = userMapper.selectById(review.getUserId());
        if (user != null) {
            response.setUserName(user.getDisplayName());
            response.setUserAvatar(user.getAvatarUrl());
        }

        // 获取菜品信息
        MenuItem menuItem = menuItemMapper.selectById(review.getMenuItemId());
        if (menuItem != null) {
            response.setItemName(menuItem.getName());
            response.setItemPrice(menuItem.getPrice());
            response.setItemImage(menuItem.getImageUrl());
        }

        // 获取图片列表
        List<DishReviewImage> images = dishReviewImageMapper.selectByDishReviewId(review.getId());
        List<String> imageUrls = images.stream()
                .map(DishReviewImage::getImageUrl)
                .collect(Collectors.toList());
        response.setImages(imageUrls);

        return response;
    }
}
