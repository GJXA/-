package com.xushu.campus.product.service;

import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.product.dto.CategoryDTO;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface CategoryService {

    /**
     * 创建分类
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO) throws BusinessException;

    /**
     * 更新分类
     */
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) throws BusinessException;

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId) throws BusinessException;

    /**
     * 根据ID获取分类
     */
    CategoryDTO getCategoryById(Long categoryId) throws BusinessException;

    /**
     * 获取所有一级分类
     */
    List<CategoryDTO> getRootCategories();

    /**
     * 根据父分类ID获取子分类
     */
    List<CategoryDTO> getCategoriesByParentId(Long parentId);

    /**
     * 获取分类树
     */
    List<CategoryDTO> getCategoryTree();

    /**
     * 搜索分类
     */
    List<CategoryDTO> searchCategories(String keyword);

    /**
     * 启用分类
     */
    void enableCategory(Long categoryId) throws BusinessException;

    /**
     * 禁用分类
     */
    void disableCategory(Long categoryId) throws BusinessException;

    /**
     * 获取所有启用的分类（平铺列表）
     */
    List<CategoryDTO> getAllEnabledCategories();

}