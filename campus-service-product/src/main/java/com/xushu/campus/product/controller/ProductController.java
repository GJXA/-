package com.xushu.campus.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.model.Result;
import com.xushu.campus.product.dto.*;
import com.xushu.campus.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品发布、搜索、管理接口")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "创建商品", description = "发布新的二手商品")
    public Result<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("创建商品请求: userId={}, title={}", userId, request.getTitle());

        ProductDTO productDTO = productService.createProduct(userId, request);
        return Result.success("商品发布成功", productDTO);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "更新商品", description = "更新商品信息")
    public Result<ProductDTO> updateProduct(
            @PathVariable @Parameter(description = "商品ID") Long productId,
            @Valid @RequestBody UpdateProductRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("更新商品请求: productId={}, userId={}", productId, userId);

        ProductDTO productDTO = productService.updateProduct(productId, userId, request);
        return Result.success("商品更新成功", productDTO);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "删除商品", description = "删除指定的商品")
    public Result<String> deleteProduct(
            @PathVariable @Parameter(description = "商品ID") Long productId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("删除商品请求: productId={}, userId={}", productId, userId);

        productService.deleteProduct(productId, userId);
        return Result.success("商品删除成功");
    }

    @GetMapping("/{productId}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    public Result<ProductDTO> getProductById(
            @PathVariable @Parameter(description = "商品ID") Long productId,
            HttpServletRequest httpRequest) {
        log.debug("获取商品详情请求: productId={}", productId);

        // 增加浏览次数
        productService.incrementViewCount(productId);

        ProductDTO productDTO = productService.getProductById(productId);
        return Result.success(productDTO);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据条件搜索商品")
    public Result<Page<ProductDTO>> searchProducts(
            @Valid ProductSearchRequest request) {
        log.debug("搜索商品请求: keyword={}, categoryId={}", request.getKeyword(), request.getCategoryId());

        Page<ProductDTO> result = productService.searchProducts(request);
        return Result.success(result);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户商品列表", description = "获取指定用户发布的商品列表")
    public Result<Page<ProductDTO>> getUserProducts(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.debug("获取用户商品列表请求: userId={}, page={}, size={}", userId, page, size);

        Page<ProductDTO> result = productService.getUserProducts(userId, page, size);
        return Result.success(result);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "获取分类商品列表", description = "获取指定分类下的商品列表")
    public Result<Page<ProductDTO>> getProductsByCategory(
            @PathVariable @Parameter(description = "分类ID") Long categoryId,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.debug("获取分类商品列表请求: categoryId={}, page={}, size={}", categoryId, page, size);

        Page<ProductDTO> result = productService.getProductsByCategory(categoryId, page, size);
        return Result.success(result);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的商品列表", description = "获取当前用户发布的商品列表")
    public Result<Page<ProductDTO>> getMyProducts(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("获取我的商品列表请求: userId={}, page={}, size={}", userId, page, size);

        Page<ProductDTO> result = productService.getUserProducts(userId, page, size);
        return Result.success(result);
    }

    @PutMapping("/{productId}/status")
    @Operation(summary = "更新商品状态", description = "更新商品状态（管理员或卖家权限）")
    public Result<String> updateProductStatus(
            @PathVariable @Parameter(description = "商品ID") Long productId,
            @RequestParam @NotNull Integer status,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("更新商品状态请求: productId={}, status={}, userId={}", productId, status, userId);

        // TODO: 检查用户权限（管理员或商品所有者）
        productService.updateProductStatus(productId, status);
        return Result.success("商品状态更新成功");
    }

    @PostMapping("/{productId}/like")
    @Operation(summary = "点赞商品", description = "给商品点赞")
    public Result<String> likeProduct(
            @PathVariable @Parameter(description = "商品ID") Long productId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("点赞商品请求: productId={}, userId={}", productId, userId);

        productService.likeProduct(productId, userId);
        return Result.success("点赞成功");
    }

    @DeleteMapping("/{productId}/like")
    @Operation(summary = "取消点赞商品", description = "取消对商品的点赞")
    public Result<String> unlikeProduct(
            @PathVariable @Parameter(description = "商品ID") Long productId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("取消点赞商品请求: productId={}, userId={}", productId, userId);

        productService.unlikeProduct(productId, userId);
        return Result.success("取消点赞成功");
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门商品", description = "获取热门商品列表（按浏览量和点赞数排序）")
    public Result<List<ProductDTO>> getHotProducts(
            @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取热门商品请求: limit={}", limit);

        List<ProductDTO> hotProducts = productService.getHotProducts(limit);
        return Result.success(hotProducts);
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新商品", description = "获取最新发布的商品列表")
    public Result<List<ProductDTO>> getLatestProducts(
            @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取最新商品请求: limit={}", limit);

        List<ProductDTO> latestProducts = productService.getLatestProducts(limit);
        return Result.success(latestProducts);
    }

    /**
     * 从请求头中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的用户ID格式");
        }
    }

    /**
     * 检查用户角色（管理员权限）
     */
    private boolean isAdmin(HttpServletRequest request) {
        String roles = request.getHeader("X-User-Roles");
        return roles != null && roles.contains("ADMIN");
    }

}