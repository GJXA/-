package com.xushu.campus.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order {

    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号（唯一）
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品标题（冗余存储，避免商品信息变更影响历史订单）
     */
    private String productTitle;

    /**
     * 商品价格（冗余存储，下单时的价格）
     */
    private BigDecimal productPrice;

    /**
     * 商品图片（冗余存储）
     */
    private String productImage;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实际支付金额（可能包含优惠等）
     */
    private BigDecimal payAmount;

    /**
     * 支付方式：ALIPAY-支付宝，WECHAT-微信，CASH-线下现金
     */
    private String paymentMethod;

    /**
     * 支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款
     */
    private Integer paymentStatus;

    /**
     * 订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-已关闭
     */
    private Integer orderStatus;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 买家留言
     */
    private String buyerMessage;

    /**
     * 卖家留言
     */
    private String sellerMessage;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 确认收货时间
     */
    private LocalDateTime confirmTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 订单过期时间（未支付自动取消）
     */
    private LocalDateTime expireTime;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 卖家昵称（冗余存储）
     */
    private String sellerNickname;

    /**
     * 买家昵称（冗余存储）
     */
    private String buyerNickname;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

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

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;

}