package com.xushu.campus.product.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品搜索请求DTO
 */
@Data
public class ProductSearchRequest {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 最小价格
     */
    private BigDecimal minPrice;

    /**
     * 最大价格
     */
    private BigDecimal maxPrice;

    /**
     * 商品状态：0-待审核，1-上架，2-下架，3-已售出
     */
    private Integer status;

    /**
     * 用户ID（卖家）
     */
    private Long userId;

    /**
     * 排序字段：create_time, price, view_count, like_count
     */
    private String sortField = "create_time";

    /**
     * 排序方向：asc, desc
     */
    private String sortDirection = "desc";

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 20;

}