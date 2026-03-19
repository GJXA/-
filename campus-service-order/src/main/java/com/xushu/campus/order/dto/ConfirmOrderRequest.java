package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 确认收货请求
 */
@Data
@Schema(description = "确认收货请求")
public class ConfirmOrderRequest {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @Schema(description = "确认收货备注")
    private String confirmNote;
}