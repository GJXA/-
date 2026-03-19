package com.xushu.campus.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.product.dto.CategoryDTO;
import com.xushu.campus.product.entity.Category;
import com.xushu.campus.product.entity.Product;
import com.xushu.campus.product.mapper.CategoryMapper;
import com.xushu.campus.product.mapper.ProductMapper;
import com.xushu.campus.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    /**
     * 分类状态常量
     */
    private static class CategoryStatus {
        static final int DISABLED = 0;    // 禁用
        static final int ENABLED = 1;     // 启用
    }

    /**
     * 分类层级常量
     */
    private static class CategoryLevel {
        static final int ROOT = 1;        // 一级分类
        static final int CHILD = 2;       // 二级分类
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("创建分类: name={}, parentId={}", categoryDTO.getName(), categoryDTO.getParentId());

        // 1. 验证分类名称唯一性
        validateCategoryName(categoryDTO.getName(), null);

        // 2. 验证父分类（如果存在）
        if (categoryDTO.getParentId() != null && categoryDTO.getParentId() > 0) {
            Category parentCategory = categoryMapper.selectById(categoryDTO.getParentId());
            if (parentCategory == null) {
                throw BusinessException.paramError("父分类不存在");
            }
            if (parentCategory.getStatus() == CategoryStatus.DISABLED) {
                throw BusinessException.paramError("父分类已被禁用");
            }
            categoryDTO.setLevel(CategoryLevel.CHILD);
        } else {
            categoryDTO.setParentId(0L);
            categoryDTO.setLevel(CategoryLevel.ROOT);
        }

        // 3. 设置默认值
        if (categoryDTO.getSort() == null) {
            categoryDTO.setSort(0);
        }
        if (categoryDTO.getStatus() == null) {
            categoryDTO.setStatus(CategoryStatus.ENABLED);
        }

        // 4. 创建分类实体
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 5. 保存分类
        categoryMapper.insert(category);

        // 6. 返回DTO
        CategoryDTO result = new CategoryDTO();
        BeanUtils.copyProperties(category, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        log.info("更新分类: categoryId={}", categoryId);

        // 1. 获取现有分类
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }

        // 2. 验证分类名称唯一性（排除自身）
        if (StringUtils.hasText(categoryDTO.getName()) && !categoryDTO.getName().equals(category.getName())) {
            validateCategoryName(categoryDTO.getName(), categoryId);
        }

        // 3. 验证父分类（如果修改）
        if (categoryDTO.getParentId() != null && !categoryDTO.getParentId().equals(category.getParentId())) {
            // 不能将自己设为父分类
            if (categoryDTO.getParentId().equals(categoryId)) {
                throw BusinessException.paramError("不能将自己设为父分类");
            }

            // 验证新父分类
            Category parentCategory = categoryMapper.selectById(categoryDTO.getParentId());
            if (parentCategory == null) {
                throw BusinessException.paramError("父分类不存在");
            }
            if (parentCategory.getStatus() == CategoryStatus.DISABLED) {
                throw BusinessException.paramError("父分类已被禁用");
            }

            // 更新层级
            categoryDTO.setLevel(parentCategory.getLevel() + 1);
        }

        // 4. 更新字段
        if (StringUtils.hasText(categoryDTO.getName())) {
            category.setName(categoryDTO.getName());
        }

        if (categoryDTO.getParentId() != null) {
            category.setParentId(categoryDTO.getParentId());
            category.setLevel(categoryDTO.getLevel());
        }

        if (categoryDTO.getSort() != null) {
            category.setSort(categoryDTO.getSort());
        }

        if (categoryDTO.getStatus() != null) {
            // 如果禁用分类，需要检查是否有子分类
            if (categoryDTO.getStatus() == CategoryStatus.DISABLED && category.getLevel() == CategoryLevel.ROOT) {
                checkHasEnabledChildren(categoryId);
            }
            category.setStatus(categoryDTO.getStatus());
        }

        // 5. 保存更新
        categoryMapper.updateById(category);

        // 6. 返回更新后的DTO
        CategoryDTO result = new CategoryDTO();
        BeanUtils.copyProperties(category, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        log.info("删除分类: categoryId={}", categoryId);

        // 1. 获取分类
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }

        // 2. 检查是否有子分类
        if (category.getLevel() == CategoryLevel.ROOT) {
            List<Category> children = categoryMapper.selectByParentId(categoryId);
            if (!CollectionUtils.isEmpty(children)) {
                throw BusinessException.paramError("该分类下有子分类，不能删除");
            }
        }

        // 3. 检查是否有商品使用此分类
        checkCategoryUsedByProducts(categoryId);

        // 4. 删除分类
        categoryMapper.deleteById(categoryId);
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        log.debug("获取分类详情: categoryId={}", categoryId);

        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }

        CategoryDTO categoryDTO = new CategoryDTO();
        BeanUtils.copyProperties(category, categoryDTO);

        // 如果是根分类，加载子分类
        if (category.getLevel() == CategoryLevel.ROOT) {
            List<Category> children = categoryMapper.selectByParentId(categoryId);
            List<CategoryDTO> childDTOs = children.stream()
                    .map(child -> {
                        CategoryDTO childDTO = new CategoryDTO();
                        BeanUtils.copyProperties(child, childDTO);
                        return childDTO;
                    })
                    .collect(Collectors.toList());
            categoryDTO.setChildren(childDTOs);
        }

        return categoryDTO;
    }

    @Override
    public List<CategoryDTO> getRootCategories() {
        log.debug("获取所有一级分类");

        List<Category> rootCategories = categoryMapper.selectRootCategories();
        return rootCategories.stream()
                .map(category -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    BeanUtils.copyProperties(category, categoryDTO);
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategoriesByParentId(Long parentId) {
        log.debug("获取子分类: parentId={}", parentId);

        List<Category> childCategories = categoryMapper.selectByParentId(parentId);
        return childCategories.stream()
                .map(category -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    BeanUtils.copyProperties(category, categoryDTO);
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategoryTree() {
        log.debug("获取分类树");

        // 1. 获取所有启用的分类
        List<Category> allCategories = categoryMapper.selectAllEnabled();

        // 2. 构建分类树
        Map<Long, CategoryDTO> categoryMap = new HashMap<>();
        List<CategoryDTO> rootCategories = new ArrayList<>();

        // 第一遍：创建所有DTO并建立映射
        for (Category category : allCategories) {
            CategoryDTO categoryDTO = new CategoryDTO();
            BeanUtils.copyProperties(category, categoryDTO);
            categoryDTO.setChildren(new ArrayList<>());
            categoryMap.put(category.getId(), categoryDTO);
        }

        // 第二遍：构建树形结构
        for (Category category : allCategories) {
            CategoryDTO categoryDTO = categoryMap.get(category.getId());

            if (category.getParentId() == 0) {
                // 根分类
                rootCategories.add(categoryDTO);
            } else {
                // 子分类，添加到父分类的children中
                CategoryDTO parentDTO = categoryMap.get(category.getParentId());
                if (parentDTO != null) {
                    parentDTO.getChildren().add(categoryDTO);
                }
            }
        }

        // 按sort排序
        rootCategories.sort(Comparator.comparingInt(CategoryDTO::getSort));
        for (CategoryDTO root : rootCategories) {
            if (!CollectionUtils.isEmpty(root.getChildren())) {
                root.getChildren().sort(Comparator.comparingInt(CategoryDTO::getSort));
            }
        }

        return rootCategories;
    }

    @Override
    public List<CategoryDTO> searchCategories(String keyword) {
        log.debug("搜索分类: keyword={}", keyword);

        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        List<Category> categories = categoryMapper.selectByName(keyword);
        return categories.stream()
                .map(category -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    BeanUtils.copyProperties(category, categoryDTO);
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableCategory(Long categoryId) {
        log.info("启用分类: categoryId={}", categoryId);

        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }

        if (category.getStatus() == CategoryStatus.ENABLED) {
            return; // 已经是启用状态
        }

        // 如果启用子分类，需要确保父分类也是启用的
        if (category.getLevel() == CategoryLevel.CHILD) {
            Category parentCategory = categoryMapper.selectById(category.getParentId());
            if (parentCategory == null || parentCategory.getStatus() == CategoryStatus.DISABLED) {
                throw BusinessException.paramError("父分类未启用，不能启用子分类");
            }
        }

        category.setStatus(CategoryStatus.ENABLED);
        categoryMapper.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableCategory(Long categoryId) {
        log.info("禁用分类: categoryId={}", categoryId);

        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }

        if (category.getStatus() == CategoryStatus.DISABLED) {
            return; // 已经是禁用状态
        }

        // 如果禁用根分类，需要同时禁用所有子分类
        if (category.getLevel() == CategoryLevel.ROOT) {
            List<Category> children = categoryMapper.selectByParentId(categoryId);
            for (Category child : children) {
                child.setStatus(CategoryStatus.DISABLED);
                categoryMapper.updateById(child);
            }
        }

        category.setStatus(CategoryStatus.DISABLED);
        categoryMapper.updateById(category);
    }

    @Override
    public List<CategoryDTO> getAllEnabledCategories() {
        log.debug("获取所有启用的分类");

        List<Category> categories = categoryMapper.selectAllEnabled();
        return categories.stream()
                .map(category -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    BeanUtils.copyProperties(category, categoryDTO);
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 验证分类名称唯一性
     */
    private void validateCategoryName(String name, Long excludeId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, name);

        if (excludeId != null) {
            queryWrapper.ne(Category::getId, excludeId);
        }

        Long count = categoryMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw BusinessException.paramError("分类名称已存在");
        }
    }

    /**
     * 检查分类是否有启用的子分类
     */
    private void checkHasEnabledChildren(Long parentId) {
        List<Category> children = categoryMapper.selectByParentId(parentId);
        boolean hasEnabledChild = children.stream()
                .anyMatch(child -> child.getStatus() == CategoryStatus.ENABLED);

        if (hasEnabledChild) {
            throw BusinessException.paramError("该分类下有启用的子分类，不能禁用");
        }
    }

    /**
     * 检查分类是否被商品使用
     */
    private void checkCategoryUsedByProducts(Long categoryId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCategoryId, categoryId)
                .eq(Product::getDeleted, 0);

        Long productCount = productMapper.selectCount(queryWrapper);
        if (productCount > 0) {
            throw BusinessException.paramError("该分类下有商品，不能删除");
        }
    }

}