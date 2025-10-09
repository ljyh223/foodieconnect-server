package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.tabletalk.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 用户Mapper接口
 */
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND status = 'ACTIVE'")
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * 检查手机号是否存在
     */
    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE phone = #{phone})")
    boolean existsByPhone(@Param("phone") String phone);
}