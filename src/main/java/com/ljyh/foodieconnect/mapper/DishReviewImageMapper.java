package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.DishReviewImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜品评价图片Mapper接口
 */
@Mapper
public interface DishReviewImageMapper extends BaseMapper<DishReviewImage> {

    /**
     * 根据评价ID查询图片列表
     *
     * @param dishReviewId 菜品评价ID
     * @return 图片列表
     */
    @Select("SELECT * FROM dish_review_images WHERE dish_review_id = #{dishReviewId} ORDER BY sort_order ASC")
    List<DishReviewImage> selectByDishReviewId(Long dishReviewId);

    /**
     * 根据评价ID删除图片
     *
     * @param dishReviewId 菜品评价ID
     * @return 删除数量
     */
    int deleteByDishReviewId(Long dishReviewId);
}
