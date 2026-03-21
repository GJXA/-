package com.xushu.campus.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 发货信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_delivery")
public class OrderDelivery {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 发货备注
     */
    private String deliveryNote;

    /**
     * 发货状态：0-已发货，1-运输中，2-已签收
     */
    private Integer deliveryStatus;

    /**
     * 预计到达时间
     */
    private LocalDateTime estimatedArrival;

    /**
     * 实际到达时间
     */
    private LocalDateTime actualArrival;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}