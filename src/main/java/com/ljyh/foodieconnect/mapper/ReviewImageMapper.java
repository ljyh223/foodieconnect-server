package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.ReviewImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评论图片Mapper接口
 */
@Mapper
public interface ReviewImageMapper extends BaseMapper<ReviewImage> {
    
    /**
     * 根据评论ID查询图片列表
     * 
     * @param reviewId 评论ID
     * @return 图片列表
     */
    List<ReviewImage> selectByReviewId(Long reviewId);
    
    /**
     * 根据评论ID删除图片
     * 
     * @param reviewId 评论ID
     * @return 删除数量
     */
    int deleteByReviewId(Long reviewId);
}