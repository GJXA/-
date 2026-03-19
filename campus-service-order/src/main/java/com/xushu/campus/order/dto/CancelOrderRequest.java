package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 取消订单请求
 */
@Data
@Schema(description = "取消订单请求")
public class CancelOrderRequest {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @NotBlank(message = "取消原因不能为空")
    @Schema(description = "取消原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cancelReason;
}