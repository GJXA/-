package com.xushu.campus.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_refund")
public class OrderRefund {

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
     * 退款单号（唯一）
     */
    private String refundNo;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款说明
     */
    private String refundDescription;

    /**
     * 退款状态：0-待处理，1-已批准，2-已拒绝，3-已完成
     */
    private Integer refundStatus;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人类型：BUYER-买家，SELLER-卖家
     */
    private String applicantType;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批备注
     */
    private String approveRemark;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款凭证
     */
    private String refundVoucher;

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