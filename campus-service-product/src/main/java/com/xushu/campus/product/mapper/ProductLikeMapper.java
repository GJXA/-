package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.ProductLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品点赞Mapper接口
 */
@Mapper
public interface ProductLikeMapper extends BaseMapper<ProductLike> {

    // 可以根据需要添加自定义查询方法

}