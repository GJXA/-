package com.xushu.campus.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("products")
public class Product {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（卖家）
     */
    private Long userId;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品状态：0-待审核，1-上架，2-下架，3-已售出
     */
    private Integer status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 商品图片URL列表（JSON格式存储）
     */
    private String images;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 成色：NEW-全新，LIKE_NEW-几乎全新，USED-使用过
     */
    private String qualityLevel;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;


    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 置顶权重
     */
    private Integer topWeight;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isRecommended;






    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;
}