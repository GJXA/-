package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 发货请求
 */
@Data
@Schema(description = "发货请求")
public class DeliveryOrderRequest {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @NotBlank(message = "快递公司不能为空")
    @Schema(description = "快递公司", requiredMode = Schema.RequiredMode.REQUIRED)
    private String expressCompany;

    @NotBlank(message = "快递单号不能为空")
    @Schema(description = "快递单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String expressNumber;

    @Schema(description = "发货备注")
    private String deliveryNote;
}