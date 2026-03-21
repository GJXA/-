package com.xushu.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.order.entity.OrderRefund;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单退款Mapper接口
 */
@Mapper
public interface OrderRefundMapper extends BaseMapper<OrderRefund> {

    // 可以根据需要添加自定义查询方法

}