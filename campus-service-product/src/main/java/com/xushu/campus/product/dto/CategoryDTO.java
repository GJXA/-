package com.xushu.campus.product.dto;

import lombok.Data;

import java.util.List;

/**
 * 商品分类数据传输对象
 */
@Data
public class CategoryDTO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类层级：1-一级分类，2-二级分类
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 子分类列表
     */
    private List<CategoryDTO> children;

}