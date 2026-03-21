package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.ProductStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品统计Mapper接口
 */
@Mapper
public interface ProductStatisticsMapper extends BaseMapper<ProductStatistics> {

    // 可以根据需要添加自定义查询方法

}