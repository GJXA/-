package com.xushu.campus.order.feign.fallback;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.order.feign.ProductFeignClient;
import com.xushu.campus.product.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品服务Feign客户端降级处理
 */
@Slf4j
@Component
public class ProductFeignClientFallback implements ProductFeignClient {

    @Override
    public Result<ProductDTO> getProductById(Long productId) {
        log.warn("商品服务不可用，触发降级: getProductById, productId={}", productId);
        // 返回空商品信息，业务层需要处理这种情况
        return Result.success(new ProductDTO());
    }

    @Override
    public Result<List<ProductDTO>> getProductsByIds(List<Long> productIds) {
        log.warn("商品服务不可用，触发降级: getProductsByIds, productIds={}", productIds);
        // 返回空列表
        return Result.success(new ArrayList<>());
    }

}