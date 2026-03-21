package com.xushu.campus.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单操作记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_operation_log")
public class OrderOperationLog {

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
     * 操作类型：CREATE-创建，CANCEL-取消，PAY-支付，DELIVER-发货，CONFIRM_RECEIPT-确认收货，APPLY_REFUND-申请退款，PROCESS_REFUND-处理退款
     */
    private String operationType;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人类型：BUYER-买家，SELLER-卖家，ADMIN-管理员，SYSTEM-系统
     */
    private String operatorType;

    /**
     * 旧状态（JSON格式）
     */
    private String oldStatus;

    /**
     * 新状态（JSON格式）
     */
    private String newStatus;

    /**
     * 操作备注
     */
    private String operationRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}