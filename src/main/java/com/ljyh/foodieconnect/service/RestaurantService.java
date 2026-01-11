package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.foodieconnect.entity.ChatRoom;
import com.ljyh.foodieconnect.entity.RecommendedDish;
import com.ljyh.foodieconnect.entity.Restaurant;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.RecommendedDishMapper;
import com.ljyh.foodieconnect.mapper.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 餐厅服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService extends ServiceImpl<RestaurantMapper, Restaurant> {
    
    private final RestaurantMapper restaurantMapper;
    private final RecommendedDishMapper recommendedDishMapper;
    private final ChatRoomService chatRoomService;
    
    /**
     * 分页查询餐厅列表
     */
    public Page<Restaurant> getRestaurants(int page, int size, String type, String keyword) {
        Page<Restaurant> pageParam = new Page<>(page, size);
        
        QueryWrapper<Restaurant> wrapper = new QueryWrapper<>();
        wrapper.eq("is_open", true);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("description", keyword));
        }
        
        wrapper.orderByDesc("rating", "review_count");
        
        return restaurantMapper.selectPage(pageParam, wrapper);
    }
    
    /**
     * 根据ID获取餐厅详情
     */
    public Restaurant getRestaurantById(Long id) {
        Restaurant restaurant = restaurantMapper.selectById(id);
        if (restaurant == null) {
            throw new BusinessException("RESTAURANT_NOT_FOUND", "餐厅不存在");
        }
        return restaurant;
    }
    
    /**
     * 获取餐厅详情（包含推荐菜品和聊天室信息）
     */
    public Map<String, Object> getRestaurantDetail(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        
        // 获取推荐菜品
        List<RecommendedDish> recommendedDishes = recommendedDishMapper.findByRestaurantId(id);
        
        // 获取餐厅聊天室信息
        ChatRoom chatRoom = chatRoomService.getRestaurantChatRoom(id);
        if (chatRoom != null) {
            // 创建安全的聊天室信息对象，不包含敏感信息如验证码
            Map<String, Object> safeChatRoomInfo = Map.of(
                "id", chatRoom.getId(),
                "name", chatRoom.getName(),
                "status", chatRoom.getStatus(),
                "onlineUserCount", chatRoom.getOnlineUserCount() != null ? chatRoom.getOnlineUserCount() : 0
            );
            
            return Map.of(
                "restaurant", restaurant,
                "recommendedDishes", recommendedDishes,
                "chatRoom", safeChatRoomInfo
            );
        }
        
        return Map.of(
            "restaurant", restaurant,
            "recommendedDishes", recommendedDishes
        );
    }
    
    /**
     * 搜索餐厅
     */
    public Page<Restaurant> searchRestaurants(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getRestaurants(page, size, null, null);
        }
        
        Page<Restaurant> pageParam = new Page<>(page, size);
        return restaurantMapper.searchByKeyword(pageParam, keyword.trim());
    }
    
    /**
     * 根据类型查询餐厅
     */
    public Page<Restaurant> getRestaurantsByType(String type, int page, int size) {
        Page<Restaurant> pageParam = new Page<>(page, size);
        return restaurantMapper.findByType(pageParam, type);
    }
    
    /**
     * 获取热门餐厅
     */
    public List<Restaurant> getPopularRestaurants(int limit) {
        return restaurantMapper.findPopularRestaurants(limit);
    }
    
    /**
     * 根据评分范围查询餐厅
     */
    public Page<Restaurant> getRestaurantsByRatingRange(Double minRating, Double maxRating, int page, int size) {
        Page<Restaurant> pageParam = new Page<>(page, size);
        return restaurantMapper.findByRatingRange(pageParam, minRating, maxRating);
    }
    
    /**
     * 获取所有餐厅类型
     */
    public List<String> getAllRestaurantTypes() {
        QueryWrapper<Restaurant> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT type");
        wrapper.eq("is_open", true);
        wrapper.orderByAsc("type");
        
        List<Restaurant> restaurants = restaurantMapper.selectList(wrapper);
        return restaurants.stream()
                .map(Restaurant::getType)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新餐厅评分
     */
    public void updateRestaurantRating(Long restaurantId) {
        // 计算平均评分（这里简化处理，实际应该从评论表中计算）
        Double averageRating = 4.5; // 默认评分
        Integer reviewCount = 10; // 默认评论数量
        
        // 更新餐厅信息
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setRating(java.math.BigDecimal.valueOf(averageRating));
        restaurant.setReviewCount(reviewCount);
        
        restaurantMapper.updateById(restaurant);
        log.info("更新餐厅评分: {} -> {}", restaurant.getName(), averageRating);
    }
}