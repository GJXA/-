package com.xushu.campus.order.feign;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.order.feign.fallback.ProductFeignClientFallback;
import com.xushu.campus.product.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 商品服务Feign客户端
 */
@FeignClient(name = "campus-service-product", fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {

    /**
     * 根据商品ID获取商品信息
     */
    @GetMapping("/api/products/{productId}")
    Result<ProductDTO> getProductById(@PathVariable("productId") Long productId);

    /**
     * 根据商品ID列表批量获取商品信息
     */
    @PostMapping("/api/products/batch")
    Result<List<ProductDTO>> getProductsByIds(@RequestBody List<Long> productIds);

}