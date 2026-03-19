package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单统计DTO
 */
@Data
@Schema(description = "订单统计信息")
public class OrderStatisticsDTO {

    @Schema(description = "待付款订单数")
    private Integer pendingPaymentCount;

    @Schema(description = "待发货订单数")
    private Integer pendingDeliveryCount;

    @Schema(description = "待收货订单数")
    private Integer pendingReceiptCount;

    @Schema(description = "已完成订单数")
    private Integer completedCount;

    @Schema(description = "已取消订单数")
    private Integer cancelledCount;

    @Schema(description = "总订单数")
    private Integer totalCount;

    @Schema(description = "总交易金额")
    private BigDecimal totalAmount;

    @Schema(description = "今日订单数")
    private Integer todayOrderCount;

    @Schema(description = "今日交易金额")
    private BigDecimal todayAmount;

    @Schema(description = "本月订单数")
    private Integer monthOrderCount;

    @Schema(description = "本月交易金额")
    private BigDecimal monthAmount;

    /**
     * 计算总订单数
     */
    public void calculateTotalCount() {
        this.totalCount = (this.pendingPaymentCount != null ? this.pendingPaymentCount : 0)
                + (this.pendingDeliveryCount != null ? this.pendingDeliveryCount : 0)
                + (this.pendingReceiptCount != null ? this.pendingReceiptCount : 0)
                + (this.completedCount != null ? this.completedCount : 0)
                + (this.cancelledCount != null ? this.cancelledCount : 0);
    }
}