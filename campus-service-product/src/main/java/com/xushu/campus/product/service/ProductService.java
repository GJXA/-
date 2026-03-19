package com.xushu.campus.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.product.dto.*;
import com.xushu.campus.product.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 创建商品
     */
    ProductDTO createProduct(Long userId, CreateProductRequest request) throws BusinessException;

    /**
     * 更新商品
     */
    ProductDTO updateProduct(Long productId, Long userId, UpdateProductRequest request) throws BusinessException;

    /**
     * 删除商品
     */
    void deleteProduct(Long productId, Long userId) throws BusinessException;

    /**
     * 根据ID获取商品详情
     */
    ProductDTO getProductById(Long productId) throws BusinessException;

    /**
     * 搜索商品
     */
    Page<ProductDTO> searchProducts(ProductSearchRequest request);

    /**
     * 获取用户发布的商品列表
     */
    Page<ProductDTO> getUserProducts(Long userId, Integer page, Integer size);

    /**
     * 获取分类下的商品列表
     */
    Page<ProductDTO> getProductsByCategory(Long categoryId, Integer page, Integer size);

    /**
     * 更新商品状态
     */
    void updateProductStatus(Long productId, Integer status) throws BusinessException;

    /**
     * 批量更新商品状态
     */
    void batchUpdateProductStatus(List<Long> productIds, Integer status) throws BusinessException;

    /**
     * 增加商品浏览次数
     */
    void incrementViewCount(Long productId);

    /**
     * 点赞商品
     */
    void likeProduct(Long productId, Long userId) throws BusinessException;

    /**
     * 取消点赞商品
     */
    void unlikeProduct(Long productId, Long userId) throws BusinessException;

    /**
     * 获取热门商品
     */
    List<ProductDTO> getHotProducts(int limit);

    /**
     * 获取最新商品
     */
    List<ProductDTO> getLatestProducts(int limit);

}