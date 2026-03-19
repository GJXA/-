package com.xushu.campus.product.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建商品请求DTO
 */
@Data
public class CreateProductRequest {

    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为空")
    @Positive(message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
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

}