package com.xushu.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.order.entity.OrderDelivery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单物流Mapper接口
 */
@Mapper
public interface OrderDeliveryMapper extends BaseMapper<OrderDelivery> {

    // 可以根据需要添加自定义查询方法

}