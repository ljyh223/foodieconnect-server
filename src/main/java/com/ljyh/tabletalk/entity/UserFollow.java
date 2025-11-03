package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户关注关系实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_follows")
public class UserFollow {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关注者ID
     */
    private Long followerId;
    
    /**
     * 被关注者ID
     */
    private Long followingId;
    
    /**
     * 关注时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}