package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品图片Mapper接口
 */
@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {

    // 可以根据需要添加自定义查询方法

}