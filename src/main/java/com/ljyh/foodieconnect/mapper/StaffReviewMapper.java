package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.entity.StaffReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 店员评价Mapper接口
 */
@Mapper
public interface StaffReviewMapper extends BaseMapper<StaffReview> {
    
    /**
     * 根据店员ID分页查询评价（包含用户信息）
     */
    Page<StaffReview> findByStaffId(Page<StaffReview> page, @Param("staffId") Long staffId);
    
    /**
     * 根据用户ID查询评价
     */
    List<StaffReview> findByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否已评价过该店员
     */
    @Select("SELECT COUNT(*) FROM staff_reviews WHERE staff_id = #{staffId} AND user_id = #{userId}")
    boolean existsByStaffIdAndUserId(@Param("staffId") Long staffId, @Param("userId") Long userId);
    
    /**
     * 计算店员平均评分
     */
    @Select("SELECT AVG(rating) FROM staff_reviews WHERE staff_id = #{staffId}")
    Double calculateAverageRating(@Param("staffId") Long staffId);
    
    /**
     * 统计店员评价数量
     */
    @Select("SELECT COUNT(*) FROM staff_reviews WHERE staff_id = #{staffId}")
    Integer countByStaffId(@Param("staffId") Long staffId);
}