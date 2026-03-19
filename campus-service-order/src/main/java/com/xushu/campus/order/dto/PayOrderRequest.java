package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 支付订单请求
 */
@Data
@Schema(description = "支付订单请求")
public class PayOrderRequest {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @NotBlank(message = "支付方式不能为空")
    @Schema(description = "支付方式: ALIPAY-支付宝, WECHAT-微信, CASH-现金", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALIPAY")
    private String paymentMethod;

    @Schema(description = "支付密码（模拟支付）")
    private String payPassword;

    @Schema(description = "支付凭证（如支付宝交易号、微信支付订单号等）")
    private String payVoucher;
}