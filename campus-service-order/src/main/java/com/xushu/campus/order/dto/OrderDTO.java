package com.xushu.campus.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单DTO
 */
@Data
@Schema(description = "订单信息")
public class OrderDTO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品标题")
    private String productTitle;

    @Schema(description = "商品价格")
    private BigDecimal productPrice;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "实际支付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "支付方式描述")
    private String paymentMethodDesc;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "支付状态描述")
    private String paymentStatusDesc;

    @Schema(description = "订单状态")
    private Integer orderStatus;

    @Schema(description = "订单状态描述")
    private String orderStatusDesc;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "买家留言")
    private String buyerMessage;

    @Schema(description = "卖家留言")
    private String sellerMessage;

    @Schema(description = "卖家ID")
    private Long sellerId;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "确认收货时间")
    private LocalDateTime confirmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "取消原因")
    private String cancelReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单过期时间")
    private LocalDateTime expireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 计算状态描述
     */
    public void calculateDesc() {
        if (this.paymentMethod != null) {
            this.paymentMethodDesc = com.xushu.campus.order.constant.OrderConstants.PaymentMethod.getDesc(this.paymentMethod);
        }
        if (this.paymentStatus != null) {
            this.paymentStatusDesc = com.xushu.campus.order.constant.OrderConstants.PaymentStatus.getDesc(this.paymentStatus);
        }
        if (this.orderStatus != null) {
            this.orderStatusDesc = com.xushu.campus.order.constant.OrderConstants.OrderStatus.getDesc(this.orderStatus);
        }
    }
}