package com.xushu.campus.product.dto;

import lombok.Data;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/**
 * 更新商品请求DTO
 */
@Data
public class UpdateProductRequest {

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
    @Positive(message = "商品价格必须大于0")
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
     * 商品状态：0-待审核，1-上架，2-下架，3-已售出
     */
    private Integer status;

}