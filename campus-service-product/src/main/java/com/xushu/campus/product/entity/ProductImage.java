package com.xushu.campus.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品图片实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_images")
public class ProductImage {

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
     * 图片URL
     */
    private String imageUrl;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否主图：0-否，1-是
     */
    private Integer isMain;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}