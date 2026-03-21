package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.ProductComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评论Mapper接口
 */
@Mapper
public interface ProductCommentMapper extends BaseMapper<ProductComment> {

    // 可以根据需要添加自定义查询方法

}