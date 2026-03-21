package com.xushu.campus.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_comments")
public class ProductComment {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评分（1-5）
     */
    private Integer rating;

    /**
     * 状态：0-隐藏，1-显示，2-审核中
     */
    private Integer status;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}