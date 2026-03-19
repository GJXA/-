package com.xushu.campus.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品数据传输对象
 */
@Data
public class ProductDTO {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 用户ID（卖家）
     */
    private Long userId;

    /**
     * 卖家信息（暂时使用Object，后续通过Feign获取）
     */
    private Object seller;

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
     * 分类信息
     */
    private CategoryDTO category;

    /**
     * 商品状态：0-待审核，1-上架，2-下架，3-已售出
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 商品图片URL列表
     */
    private List<String> images;

    /**
     * 商品位置
     */
    private String location;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否已点赞（当前用户）
     */
    private Boolean liked;

}