package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.tabletalk.entity.Merchant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商家Mapper接口
 */
public interface MerchantMapper extends BaseMapper<Merchant> {
    
    /**
     * 根据用户名查找商家
     */
    @Select("SELECT * FROM merchants WHERE username = #{username}")
    Merchant findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查找商家
     */
    @Select("SELECT * FROM merchants WHERE email = #{email}")
    Merchant findByEmail(@Param("email") String email);
    
    /**
     * 根据餐厅ID查找商家列表
     */
    @Select("SELECT * FROM merchants WHERE restaurant_id = #{restaurantId} ORDER BY role ASC, created_at DESC")
    java.util.List<Merchant> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据餐厅ID和角色查找商家
     */
    @Select("SELECT * FROM merchants WHERE restaurant_id = #{restaurantId} AND role = #{role}")
    java.util.List<Merchant> findByRestaurantIdAndRole(@Param("restaurantId") Long restaurantId, 
                                                   @Param("role") Merchant.MerchantRole role);
}