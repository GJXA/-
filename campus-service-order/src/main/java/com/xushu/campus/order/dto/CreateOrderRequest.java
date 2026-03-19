package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建订单请求
 */
@Data
@Schema(description = "创建订单请求")
public class CreateOrderRequest {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    @Schema(description = "商品数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer quantity;

    @NotBlank(message = "支付方式不能为空")
    @Schema(description = "支付方式: ALIPAY-支付宝, WECHAT-微信, CASH-现金", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALIPAY")
    private String paymentMethod;

    @NotBlank(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Schema(description = "收货人电话", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    @Schema(description = "收货地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiverAddress;

    @Schema(description = "买家留言")
    private String buyerMessage;

    @Schema(description = "商品价格（用于校验）")
    private BigDecimal productPrice;

    @Schema(description = "商品标题（用于校验）")
    private String productTitle;

    @Schema(description = "商品图片（用于校验）")
    private String productImage;
}