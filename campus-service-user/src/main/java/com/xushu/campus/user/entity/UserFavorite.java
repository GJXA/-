package com.xushu.campus.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_favorites")
public class UserFavorite {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 目标类型：PRODUCT-商品，JOB-兼职
     */
    private String targetType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}