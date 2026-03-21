package com.xushu.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.order.entity.OrderOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作记录Mapper接口
 */
@Mapper
public interface OrderOperationLogMapper extends BaseMapper<OrderOperationLog> {

    // 可以根据需要添加自定义查询方法

}