package com.xushu.campus.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 订单搜索请求
 */
@Data
@Schema(description = "订单搜索请求")
public class OrderSearchRequest {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "商品标题（模糊搜索）")
    private String productTitle;

    @Schema(description = "订单状态")
    private Integer orderStatus;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "卖家ID")
    private Long sellerId;

    @Schema(description = "买家ID")
    private Long buyerId;

    @Schema(description = "页码，默认1")
    private Integer page = 1;

    @Schema(description = "每页大小，默认20")
    private Integer size = 20;

    @Schema(description = "排序字段，默认create_time")
    private String sortField = "create_time";

    @Schema(description = "排序方向，默认desc")
    private String sortDirection = "desc";
}