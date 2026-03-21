package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.ProductReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品举报Mapper接口
 */
@Mapper
public interface ProductReportMapper extends BaseMapper<ProductReport> {

    // 可以根据需要添加自定义查询方法

}