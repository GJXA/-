package com.xushu.campus.product.controller;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.product.dto.CategoryDTO;
import com.xushu.campus.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "商品分类管理", description = "商品分类查询接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    @Operation(summary = "获取所有启用的商品分类", description = "获取所有启用的商品分类列表")
    public Result<List<CategoryDTO>> getAllEnabledCategories() {
        log.debug("获取所有启用的商品分类请求");
        List<CategoryDTO> categories = categoryService.getAllEnabledCategories();
        return Result.success(categories);
    }

    @GetMapping("/categories/root")
    @Operation(summary = "获取所有一级分类", description = "获取所有一级分类（无父分类）")
    public Result<List<CategoryDTO>> getRootCategories() {
        log.debug("获取所有一级分类请求");
        List<CategoryDTO> categories = categoryService.getRootCategories();
        return Result.success(categories);
    }

    @GetMapping("/categories/tree")
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    public Result<List<CategoryDTO>> getCategoryTree() {
        log.debug("获取分类树请求");
        List<CategoryDTO> categoryTree = categoryService.getCategoryTree();
        return Result.success(categoryTree);
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详细信息")
    public Result<CategoryDTO> getCategoryById(@PathVariable Long categoryId) {
        log.debug("获取分类详情请求: categoryId={}", categoryId);
        CategoryDTO category = categoryService.getCategoryById(categoryId);
        return Result.success(category);
    }

    @GetMapping("/categories/{categoryId}/children")
    @Operation(summary = "获取子分类列表", description = "根据父分类ID获取子分类列表")
    public Result<List<CategoryDTO>> getCategoriesByParentId(@PathVariable Long categoryId) {
        log.debug("获取子分类列表请求: parentId={}", categoryId);
        List<CategoryDTO> categories = categoryService.getCategoriesByParentId(categoryId);
        return Result.success(categories);
    }
}