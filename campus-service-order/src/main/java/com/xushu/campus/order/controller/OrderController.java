package com.xushu.campus.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.model.Result;
import com.xushu.campus.order.dto.*;
import com.xushu.campus.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 订单控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单创建、支付、发货、退款等接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "用户创建新订单")
    public Result<OrderDTO> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("创建订单请求: userId={}, productId={}", userId, request.getProductId());

        OrderDTO orderDTO = orderService.createOrder(userId, request);
        return Result.success("订单创建成功", orderDTO);
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消指定的订单")
    public Result<OrderDTO> cancelOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @Valid @RequestBody CancelOrderRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("取消订单请求: orderId={}, userId={}", orderId, userId);

        // 验证订单ID一致性
        if (!orderId.equals(request.getOrderId())) {
            return Result.paramError("订单ID不一致");
        }

        OrderDTO orderDTO = orderService.cancelOrder(userId, request);
        return Result.success("订单取消成功", orderDTO);
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "支付订单", description = "支付指定的订单")
    public Result<OrderDTO> payOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @Valid @RequestBody PayOrderRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("支付订单请求: orderId={}, userId={}", orderId, userId);

        // 验证订单ID一致性
        if (!orderId.equals(request.getOrderId())) {
            return Result.paramError("订单ID不一致");
        }

        OrderDTO orderDTO = orderService.payOrder(userId, request);
        return Result.success("支付成功", orderDTO);
    }

    @PostMapping("/{orderId}/deliver")
    @Operation(summary = "发货", description = "卖家发货")
    public Result<OrderDTO> deliverOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @Valid @RequestBody DeliveryOrderRequest request,
            HttpServletRequest httpRequest) {
        Long sellerId = getUserIdFromRequest(httpRequest);
        log.info("发货请求: orderId={}, sellerId={}", orderId, sellerId);

        // 验证订单ID一致性
        if (!orderId.equals(request.getOrderId())) {
            return Result.paramError("订单ID不一致");
        }

        OrderDTO orderDTO = orderService.deliverOrder(sellerId, request);
        return Result.success("发货成功", orderDTO);
    }

    @PostMapping("/{orderId}/confirm")
    @Operation(summary = "确认收货", description = "买家确认收货")
    public Result<OrderDTO> confirmOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @Valid @RequestBody ConfirmOrderRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("确认收货请求: orderId={}, userId={}", orderId, userId);

        // 验证订单ID一致性
        if (!orderId.equals(request.getOrderId())) {
            return Result.paramError("订单ID不一致");
        }

        OrderDTO orderDTO = orderService.confirmOrder(userId, request);
        return Result.success("确认收货成功", orderDTO);
    }

    @PostMapping("/{orderId}/refund/apply")
    @Operation(summary = "申请退款", description = "买家申请退款")
    public Result<OrderDTO> applyRefund(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @Valid @RequestBody RefundRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("申请退款请求: orderId={}, userId={}", orderId, userId);

        // 验证订单ID一致性
        if (!orderId.equals(request.getOrderId())) {
            return Result.paramError("订单ID不一致");
        }

        OrderDTO orderDTO = orderService.applyRefund(userId, request);
        return Result.success("退款申请已提交", orderDTO);
    }

    @PostMapping("/{orderId}/refund/process")
    @Operation(summary = "处理退款", description = "卖家或管理员处理退款申请")
    public Result<OrderDTO> processRefund(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @RequestParam @NotNull Boolean approve,
            @RequestParam(required = false) String remark,
            HttpServletRequest httpRequest) {
        Long operatorId = getUserIdFromRequest(httpRequest);
        log.info("处理退款请求: orderId={}, operatorId={}, approve={}", orderId, operatorId, approve);

        OrderDTO orderDTO = orderService.processRefund(operatorId, orderId, approve, remark);
        return Result.success(approve ? "退款已批准" : "退款已拒绝", orderDTO);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public Result<OrderDTO> getOrderById(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("获取订单详情请求: orderId={}, userId={}", orderId, userId);

        // 验证权限：买家或卖家可以查看自己的订单
        try {
            orderService.validateOrderPermission(orderId, userId, "BUYER");
        } catch (Exception e) {
            try {
                orderService.validateOrderPermission(orderId, userId, "SELLER");
            } catch (Exception ex) {
                // 检查是否是管理员
                if (!isAdmin(httpRequest)) {
                    return Result.forbidden("无权查看此订单");
                }
            }
        }

        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return Result.success(orderDTO);
    }

    @GetMapping("/no/{orderNo}")
    @Operation(summary = "根据订单号获取订单详情", description = "根据订单号获取订单详细信息")
    public Result<OrderDTO> getOrderByOrderNo(
            @PathVariable @Parameter(description = "订单号") String orderNo,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("根据订单号获取订单详情请求: orderNo={}, userId={}", orderNo, userId);

        OrderDTO orderDTO = orderService.getOrderByOrderNo(orderNo);

        // 验证权限
        if (!orderDTO.getUserId().equals(userId) && !orderDTO.getSellerId().equals(userId) && !isAdmin(httpRequest)) {
            return Result.forbidden("无权查看此订单");
        }

        return Result.success(orderDTO);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的订单列表", description = "获取当前用户的订单列表（买家）")
    public Result<IPage<OrderDTO>> getMyOrders(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("获取我的订单列表请求: userId={}, page={}, size={}", userId, page, size);

        IPage<OrderDTO> result = orderService.getUserOrders(userId, page, size);
        return Result.success(result);
    }

    @GetMapping("/my/status/{status}")
    @Operation(summary = "获取我的订单列表（按状态）", description = "获取当前用户指定状态的订单列表（买家）")
    public Result<IPage<OrderDTO>> getMyOrdersByStatus(
            @PathVariable @Parameter(description = "订单状态") Integer status,
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("获取我的订单列表(按状态)请求: userId={}, status={}, page={}, size={}", userId, status, page, size);

        IPage<OrderDTO> result = orderService.getUserOrdersByStatus(userId, status, page, size);
        return Result.success(result);
    }

    @GetMapping("/seller")
    @Operation(summary = "获取卖家订单列表", description = "获取当前用户的订单列表（卖家）")
    public Result<IPage<OrderDTO>> getSellerOrders(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Long sellerId = getUserIdFromRequest(httpRequest);
        log.debug("获取卖家订单列表请求: sellerId={}, page={}, size={}", sellerId, page, size);

        IPage<OrderDTO> result = orderService.getSellerOrders(sellerId, page, size);
        return Result.success(result);
    }

    @GetMapping("/seller/status/{status}")
    @Operation(summary = "获取卖家订单列表（按状态）", description = "获取当前用户指定状态的订单列表（卖家）")
    public Result<IPage<OrderDTO>> getSellerOrdersByStatus(
            @PathVariable @Parameter(description = "订单状态") Integer status,
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Long sellerId = getUserIdFromRequest(httpRequest);
        log.debug("获取卖家订单列表(按状态)请求: sellerId={}, status={}, page={}, size={}", sellerId, status, page, size);

        IPage<OrderDTO> result = orderService.getSellerOrdersByStatus(sellerId, status, page, size);
        return Result.success(result);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索订单", description = "根据条件搜索订单（管理员权限）")
    public Result<IPage<OrderDTO>> searchOrders(
            @Valid OrderSearchRequest request,
            HttpServletRequest httpRequest) {
        log.debug("搜索订单请求: {}", request);

        // 检查管理员权限
        if (!isAdmin(httpRequest)) {
            return Result.forbidden("需要管理员权限");
        }

        IPage<OrderDTO> result = orderService.searchOrders(request);
        return Result.success(result);
    }

    @GetMapping("/statistics/my")
    @Operation(summary = "获取我的订单统计", description = "获取当前用户的订单统计信息（买家）")
    public Result<OrderStatisticsDTO> getMyOrderStatistics(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("获取我的订单统计请求: userId={}", userId);

        OrderStatisticsDTO statistics = orderService.getOrderStatistics(userId);
        return Result.success(statistics);
    }

    @GetMapping("/statistics/seller")
    @Operation(summary = "获取卖家订单统计", description = "获取当前用户的订单统计信息（卖家）")
    public Result<OrderStatisticsDTO> getSellerOrderStatistics(HttpServletRequest httpRequest) {
        Long sellerId = getUserIdFromRequest(httpRequest);
        log.debug("获取卖家订单统计请求: sellerId={}", sellerId);

        OrderStatisticsDTO statistics = orderService.getSellerOrderStatistics(sellerId);
        return Result.success(statistics);
    }

    @GetMapping("/{orderId}/can-cancel")
    @Operation(summary = "检查订单是否可以取消", description = "检查订单是否可以取消")
    public Result<Boolean> canCancelOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("检查订单是否可以取消请求: orderId={}, userId={}", orderId, userId);

        // 验证权限
        try {
            orderService.validateOrderPermission(orderId, userId, "BUYER");
        } catch (Exception e) {
            return Result.forbidden("无权操作此订单");
        }

        boolean canCancel = orderService.canCancelOrder(orderId);
        return Result.success(canCancel);
    }

    @GetMapping("/{orderId}/can-pay")
    @Operation(summary = "检查订单是否可以支付", description = "检查订单是否可以支付")
    public Result<Boolean> canPayOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("检查订单是否可以支付请求: orderId={}, userId={}", orderId, userId);

        // 验证权限
        try {
            orderService.validateOrderPermission(orderId, userId, "BUYER");
        } catch (Exception e) {
            return Result.forbidden("无权操作此订单");
        }

        boolean canPay = orderService.canPayOrder(orderId);
        return Result.success(canPay);
    }

    @GetMapping("/{orderId}/can-deliver")
    @Operation(summary = "检查订单是否可以发货", description = "检查订单是否可以发货")
    public Result<Boolean> canDeliverOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("检查订单是否可以发货请求: orderId={}, userId={}", orderId, userId);

        // 验证权限
        try {
            orderService.validateOrderPermission(orderId, userId, "SELLER");
        } catch (Exception e) {
            return Result.forbidden("无权操作此订单");
        }

        boolean canDeliver = orderService.canDeliverOrder(orderId);
        return Result.success(canDeliver);
    }

    @GetMapping("/{orderId}/can-confirm")
    @Operation(summary = "检查订单是否可以确认收货", description = "检查订单是否可以确认收货")
    public Result<Boolean> canConfirmOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.debug("检查订单是否可以确认收货请求: orderId={}, userId={}", orderId, userId);

        // 验证权限
        try {
            orderService.validateOrderPermission(orderId, userId, "BUYER");
        } catch (Exception e) {
            return Result.forbidden("无权操作此订单");
        }

        boolean canConfirm = orderService.canConfirmOrder(orderId);
        return Result.success(canConfirm);
    }

    /**
     * 从请求头中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的用户ID格式");
        }
    }

    /**
     * 检查用户是否是管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        String roles = request.getHeader("X-User-Roles");
        return roles != null && roles.contains("ADMIN");
    }
}