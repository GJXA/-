package com.xushu.campus.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.common.model.Result;
import com.xushu.campus.product.dto.*;
import com.xushu.campus.product.entity.Product;
import com.xushu.campus.product.mapper.ProductMapper;
import com.xushu.campus.product.service.CategoryService;
import com.xushu.campus.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 商品状态常量
     */
    private static class ProductStatus {
        static final int PENDING = 0;     // 待审核
        static final int ON_SHELF = 1;    // 上架
        static final int OFF_SHELF = 2;   // 下架
        static final int SOLD = 3;        // 已售出
    }

    /**
     * 缓存键常量
     */
    private static class CacheKey {
        static final String PRODUCT_DETAIL = "product:detail:";    // 商品详情缓存
        static final String PRODUCT_LIKE = "product:like:";       // 商品点赞缓存
        static final String HOT_PRODUCTS = "product:hot";         // 热门商品缓存
        static final String LATEST_PRODUCTS = "product:latest";   // 最新商品缓存
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductDTO createProduct(Long userId, CreateProductRequest request) {
        log.info("创建商品: userId={}, title={}", userId, request.getTitle());

        // 1. 验证分类是否存在
        CategoryDTO category = categoryService.getCategoryById(request.getCategoryId());
        if (category == null || category.getStatus() == 0) {
            throw BusinessException.paramError("商品分类不存在或已禁用");
        }

        // 2. 创建商品实体
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        // 手动映射字段名不一致的字段
        if (StringUtils.hasText(request.getLocation())) {
            product.setAddress(request.getLocation());
        }
        product.setUserId(userId);
        product.setStatus(ProductStatus.PENDING); // 默认待审核
        product.setViewCount(0);
        product.setLikeCount(0);

        // 处理图片列表
        if (!CollectionUtils.isEmpty(request.getImages())) {
            try {
                String imagesJson = objectMapper.writeValueAsString(request.getImages());
                product.setImages(imagesJson);
            } catch (Exception e) {
                log.error("图片列表序列化失败", e);
                throw BusinessException.paramError("图片格式错误");
            }
        }

        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        // 3. 保存商品
        productMapper.insert(product);

        // 4. 清除缓存
        clearProductCache();

        // 5. 返回商品DTO
        return convertToProductDTO(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductDTO updateProduct(Long productId, Long userId, UpdateProductRequest request) {
        log.info("更新商品: productId={}, userId={}", productId, userId);

        // 1. 获取商品
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw BusinessException.notFound("商品不存在");
        }

        // 2. 验证权限（只能修改自己的商品）
        if (!product.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权修改此商品");
        }

        // 3. 验证状态（已售出的商品不能修改）
        if (product.getStatus() == ProductStatus.SOLD) {
            throw BusinessException.paramError("已售出的商品不能修改");
        }

        // 4. 更新字段
        if (StringUtils.hasText(request.getTitle())) {
            product.setTitle(request.getTitle());
        }

        if (StringUtils.hasText(request.getDescription())) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getOriginalPrice() != null) {
            product.setOriginalPrice(request.getOriginalPrice());
        }

        if (request.getCategoryId() != null) {
            // 验证新分类
            CategoryDTO category = categoryService.getCategoryById(request.getCategoryId());
            if (category == null || category.getStatus() == 0) {
                throw BusinessException.paramError("商品分类不存在或已禁用");
            }
            product.setCategoryId(request.getCategoryId());
        }

        if (!CollectionUtils.isEmpty(request.getImages())) {
            try {
                String imagesJson = objectMapper.writeValueAsString(request.getImages());
                product.setImages(imagesJson);
            } catch (Exception e) {
                log.error("图片列表序列化失败", e);
                throw BusinessException.paramError("图片格式错误");
            }
        }

        if (StringUtils.hasText(request.getLocation())) {
            product.setAddress(request.getLocation());
        }

        if (StringUtils.hasText(request.getContactPhone())) {
            product.setContactPhone(request.getContactPhone());
        }

        if (request.getStatus() != null) {
            // 验证状态转换合法性
            validateStatusTransition(product.getStatus(), request.getStatus());
            product.setStatus(request.getStatus());
        }

        product.setUpdateTime(LocalDateTime.now());

        // 5. 保存更新
        productMapper.updateById(product);

        // 6. 清除缓存
        clearProductCache(productId);

        // 7. 返回更新后的商品
        return convertToProductDTO(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long productId, Long userId) {
        log.info("删除商品: productId={}, userId={}", productId, userId);

        // 1. 获取商品
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw BusinessException.notFound("商品不存在");
        }

        // 2. 验证权限（只能删除自己的商品）
        if (!product.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权删除此商品");
        }

        // 3. 逻辑删除
        product.setDeleted(1);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        // 4. 清除缓存
        clearProductCache(productId);
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        log.debug("获取商品详情: productId={}", productId);

        // 1. 尝试从缓存获取
        String cacheKey = CacheKey.PRODUCT_DETAIL + productId;
        ProductDTO cachedProduct = (ProductDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            return cachedProduct;
        }

        // 2. 从数据库获取
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw BusinessException.notFound("商品不存在");
        }

        // 3. 转换为DTO
        ProductDTO productDTO = convertToProductDTO(product);

        // 4. 放入缓存（5分钟）
        redisTemplate.opsForValue().set(cacheKey, productDTO, 5, TimeUnit.MINUTES);

        return productDTO;
    }

    @Override
    public Page<ProductDTO> searchProducts(ProductSearchRequest request) {
        log.debug("搜索商品: keyword={}, categoryId={}", request.getKeyword(), request.getCategoryId());

        // 1. 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getDeleted, 0);

        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Product::getTitle, request.getKeyword())
                    .or()
                    .like(Product::getDescription, request.getKeyword())
            );
        }

        // 分类筛选
        if (request.getCategoryId() != null) {
            queryWrapper.eq(Product::getCategoryId, request.getCategoryId());
        }

        // 价格范围筛选
        if (request.getMinPrice() != null) {
            queryWrapper.ge(Product::getPrice, request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            queryWrapper.le(Product::getPrice, request.getMaxPrice());
        }

        // 状态筛选
        if (request.getStatus() != null) {
            queryWrapper.eq(Product::getStatus, request.getStatus());
        } else {
            // 默认只显示上架商品
            queryWrapper.eq(Product::getStatus, ProductStatus.ON_SHELF);
        }

        // 用户筛选
        if (request.getUserId() != null) {
            queryWrapper.eq(Product::getUserId, request.getUserId());
        }

        // 排序
        if ("price".equals(request.getSortField())) {
            if ("asc".equals(request.getSortDirection())) {
                queryWrapper.orderByAsc(Product::getPrice);
            } else {
                queryWrapper.orderByDesc(Product::getPrice);
            }
        } else if ("view_count".equals(request.getSortField())) {
            queryWrapper.orderByDesc(Product::getViewCount);
        } else if ("like_count".equals(request.getSortField())) {
            queryWrapper.orderByDesc(Product::getLikeCount);
        } else {
            // 默认按创建时间排序
            queryWrapper.orderByDesc(Product::getCreateTime);
        }

        // 2. 执行分页查询
        Page<Product> page = new Page<>(request.getPage(), request.getSize());
        IPage<Product> productPage = productMapper.selectPage(page, queryWrapper);

        // 3. 转换为DTO列表
        List<ProductDTO> productDTOs = productPage.getRecords().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        // 4. 构建返回结果
        Page<ProductDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(productPage, resultPage, "records");
        resultPage.setRecords(productDTOs);

        return resultPage;
    }

    @Override
    public Page<ProductDTO> getUserProducts(Long userId, Integer page, Integer size) {
        log.debug("获取用户商品列表: userId={}", userId);

        // 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getUserId, userId)
                .eq(Product::getDeleted, 0)
                .orderByDesc(Product::getCreateTime);

        // 执行分页查询
        Page<Product> productPage = new Page<>(page, size);
        IPage<Product> resultPage = productMapper.selectPage(productPage, queryWrapper);

        // 转换为DTO列表
        List<ProductDTO> productDTOs = resultPage.getRecords().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<ProductDTO> pageResult = new Page<>();
        BeanUtils.copyProperties(resultPage, pageResult, "records");
        pageResult.setRecords(productDTOs);

        return pageResult;
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Integer page, Integer size) {
        log.debug("获取分类商品列表: categoryId={}", categoryId);

        // 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCategoryId, categoryId)
                .eq(Product::getDeleted, 0)
                .eq(Product::getStatus, ProductStatus.ON_SHELF)
                .orderByDesc(Product::getCreateTime);

        // 执行分页查询
        Page<Product> productPage = new Page<>(page, size);
        IPage<Product> resultPage = productMapper.selectPage(productPage, queryWrapper);

        // 转换为DTO列表
        List<ProductDTO> productDTOs = resultPage.getRecords().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<ProductDTO> pageResult = new Page<>();
        BeanUtils.copyProperties(resultPage, pageResult, "records");
        pageResult.setRecords(productDTOs);

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long productId, Integer status) {
        log.info("更新商品状态: productId={}, status={}", productId, status);

        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw BusinessException.notFound("商品不存在");
        }

        // 验证状态转换
        validateStatusTransition(product.getStatus(), status);

        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        // 清除缓存
        clearProductCache(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateProductStatus(List<Long> productIds, Integer status) {
        log.info("批量更新商品状态: productIds={}, status={}", productIds, status);

        if (CollectionUtils.isEmpty(productIds)) {
            return;
        }

        // 批量更新状态
        productMapper.batchUpdateStatus(productIds, status);

        // 清除相关缓存
        productIds.forEach(this::clearProductCache);
    }

    @Override
    public void incrementViewCount(Long productId) {
        log.debug("增加商品浏览次数: productId={}", productId);

        try {
            productMapper.incrementViewCount(productId);
        } catch (Exception e) {
            log.error("增加商品浏览次数失败: productId={}", productId, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeProduct(Long productId, Long userId) {
        log.info("点赞商品: productId={}, userId={}", productId, userId);

        // 1. 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw BusinessException.notFound("商品不存在");
        }

        // 2. 检查是否已点赞
        String likeKey = CacheKey.PRODUCT_LIKE + productId + ":" + userId;
        Boolean hasLiked = (Boolean) redisTemplate.opsForValue().get(likeKey);
        if (hasLiked != null && hasLiked) {
            throw BusinessException.paramError("您已经点过赞了");
        }

        // 3. 增加点赞数
        productMapper.incrementLikeCount(productId);

        // 4. 记录点赞状态（24小时有效）
        redisTemplate.opsForValue().set(likeKey, true, 24, TimeUnit.HOURS);

        // 5. 清除商品详情缓存
        clearProductCache(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeProduct(Long productId, Long userId) {
        log.info("取消点赞商品: productId={}, userId={}", productId, userId);

        // 1. 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw BusinessException.notFound("商品不存在");
        }

        // 2. 检查是否已点赞
        String likeKey = CacheKey.PRODUCT_LIKE + productId + ":" + userId;
        Boolean hasLiked = (Boolean) redisTemplate.opsForValue().get(likeKey);
        if (hasLiked == null || !hasLiked) {
            throw BusinessException.paramError("您还没有点赞");
        }

        // 3. 减少点赞数
        productMapper.decrementLikeCount(productId);

        // 4. 删除点赞记录
        redisTemplate.delete(likeKey);

        // 5. 清除商品详情缓存
        clearProductCache(productId);
    }

    @Override
    public List<ProductDTO> getHotProducts(int limit) {
        log.debug("获取热门商品: limit={}", limit);

        // 1. 尝试从缓存获取
        List<ProductDTO> cachedProducts = (List<ProductDTO>) redisTemplate.opsForValue().get(CacheKey.HOT_PRODUCTS);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        // 2. 从数据库获取（按浏览量和点赞数排序）
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getDeleted, 0)
                .eq(Product::getStatus, ProductStatus.ON_SHELF)
                .orderByDesc(Product::getViewCount, Product::getLikeCount)
                .last("LIMIT " + limit);

        List<Product> products = productMapper.selectList(queryWrapper);

        // 3. 转换为DTO
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        // 4. 放入缓存（10分钟）
        redisTemplate.opsForValue().set(CacheKey.HOT_PRODUCTS, productDTOs, 10, TimeUnit.MINUTES);

        return productDTOs;
    }

    @Override
    public List<ProductDTO> getLatestProducts(int limit) {
        log.debug("获取最新商品: limit={}", limit);

        // 1. 尝试从缓存获取
        List<ProductDTO> cachedProducts = (List<ProductDTO>) redisTemplate.opsForValue().get(CacheKey.LATEST_PRODUCTS);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        // 2. 从数据库获取（按创建时间排序）
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getDeleted, 0)
                .eq(Product::getStatus, ProductStatus.ON_SHELF)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT " + limit);

        List<Product> products = productMapper.selectList(queryWrapper);

        // 3. 转换为DTO
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        // 4. 放入缓存（5分钟）
        redisTemplate.opsForValue().set(CacheKey.LATEST_PRODUCTS, productDTOs, 5, TimeUnit.MINUTES);

        return productDTOs;
    }

    /**
     * 验证状态转换的合法性
     */
    private void validateStatusTransition(Integer oldStatus, Integer newStatus) {
        if (oldStatus == null || newStatus == null) {
            return;
        }

        // 已售出的商品不能再改变状态
        if (oldStatus == ProductStatus.SOLD) {
            throw BusinessException.paramError("已售出的商品不能改变状态");
        }

        // 待审核的商品可以转为上架、下架或拒绝
        if (oldStatus == ProductStatus.PENDING) {
            if (!Arrays.asList(ProductStatus.ON_SHELF, ProductStatus.OFF_SHELF).contains(newStatus)) {
                throw BusinessException.paramError("无效的状态转换");
            }
        }

        // 上架的商品可以转为下架或已售出
        if (oldStatus == ProductStatus.ON_SHELF) {
            if (!Arrays.asList(ProductStatus.OFF_SHELF, ProductStatus.SOLD).contains(newStatus)) {
                throw BusinessException.paramError("无效的状态转换");
            }
        }

        // 下架的商品可以转为上架
        if (oldStatus == ProductStatus.OFF_SHELF) {
            if (newStatus != ProductStatus.ON_SHELF) {
                throw BusinessException.paramError("无效的状态转换");
            }
        }
    }

    /**
     * 将Product实体转换为ProductDTO
     */
    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);

        // 处理图片列表
        if (StringUtils.hasText(product.getImages())) {
            try {
                List<String> images = objectMapper.readValue(product.getImages(), new TypeReference<List<String>>() {});
                productDTO.setImages(images);
            } catch (Exception e) {
                log.error("图片列表反序列化失败", e);
                productDTO.setImages(new ArrayList<>());
            }
        } else {
            productDTO.setImages(new ArrayList<>());
        }

        // 设置状态描述
        productDTO.setStatusDesc(getStatusDesc(product.getStatus()));

        // TODO: 设置卖家信息和分类信息（需要通过Feign调用其他服务）
        // 这里暂时留空，实际项目中需要通过Feign调用用户服务和分类服务

        return productDTO;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }

        switch (status) {
            case ProductStatus.PENDING:
                return "待审核";
            case ProductStatus.ON_SHELF:
                return "上架中";
            case ProductStatus.OFF_SHELF:
                return "已下架";
            case ProductStatus.SOLD:
                return "已售出";
            default:
                return "未知";
        }
    }

    /**
     * 清除商品相关缓存
     */
    private void clearProductCache(Long productId) {
        // 清除商品详情缓存
        String detailKey = CacheKey.PRODUCT_DETAIL + productId;
        redisTemplate.delete(detailKey);

        // 清除热门和最新商品缓存
        redisTemplate.delete(CacheKey.HOT_PRODUCTS);
        redisTemplate.delete(CacheKey.LATEST_PRODUCTS);
    }

    /**
     * 清除所有商品缓存
     */
    private void clearProductCache() {
        // 清除热门和最新商品缓存
        redisTemplate.delete(CacheKey.HOT_PRODUCTS);
        redisTemplate.delete(CacheKey.LATEST_PRODUCTS);
    }

}