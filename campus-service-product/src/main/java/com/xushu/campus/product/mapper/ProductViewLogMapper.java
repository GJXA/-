package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.ProductViewLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品浏览记录Mapper接口
 */
@Mapper
public interface ProductViewLogMapper extends BaseMapper<ProductViewLog> {

    // 可以根据需要添加自定义查询方法

}