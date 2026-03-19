package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 退款请求
 */
@Data
@Schema(description = "退款请求")
public class RefundRequest {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @NotNull(message = "退款金额不能为空")
    @Schema(description = "退款金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal refundAmount;

    @NotBlank(message = "退款原因不能为空")
    @Schema(description = "退款原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refundReason;

    @Schema(description = "退款说明")
    private String refundDescription;
}