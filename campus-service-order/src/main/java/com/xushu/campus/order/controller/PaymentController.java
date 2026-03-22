package com.xushu.campus.order.controller;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.order.dto.OrderDTO;
import com.xushu.campus.order.dto.PayOrderRequest;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 模拟支付控制器
 * 注意：此控制器仅用于开发和测试环境，生产环境应接入真实的支付接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "模拟支付", description = "模拟支付接口（仅用于开发和测试）")
public class PaymentController {

    private final OrderService orderService;

    /**
     * 模拟支付页面数据
     */
    @GetMapping("/{orderId}/page")
    @Operation(summary = "获取模拟支付页面数据", description = "获取模拟支付页面所需的数据")
    public Result<Map<String, Object>> getPaymentPageData(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("获取模拟支付页面数据: orderId={}, userId={}", orderId, userId);

        // 验证订单权限
        orderService.validateOrderPermission(orderId, userId, "BUYER");

        // 获取订单信息
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        // 检查订单是否可以支付
        if (!orderService.canPayOrder(orderId)) {
            return Result.paramError("订单当前状态不支持支付");
        }

        // 构建支付页面数据
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("order", orderDTO);
        pageData.put("paymentMethods", new String[]{"ALIPAY", "WECHAT", "CASH"});
        pageData.put("timestamp", System.currentTimeMillis());
        pageData.put("nonce", UUID.randomUUID().toString());

        // 模拟支付配置
        Map<String, Object> paymentConfig = new HashMap<>();
        paymentConfig.put("mockMode", true);
        paymentConfig.put("callbackUrl", "/api/payments/callback");
        paymentConfig.put("timeoutMinutes", 30);
        pageData.put("config", paymentConfig);

        return Result.success(pageData);
    }

    /**
     * 模拟支付提交
     */
    @PostMapping("/{orderId}/submit")
    @Operation(summary = "提交模拟支付", description = "提交模拟支付请求")
    public Result<Map<String, Object>> submitPayment(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @Valid @RequestBody PayOrderRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("提交模拟支付: orderId={}, userId={}, paymentMethod={}", orderId, userId, request.getPaymentMethod());

        // 验证订单ID一致性
        if (!orderId.equals(request.getOrderId())) {
            return Result.paramError("订单ID不一致");
        }

        // 验证订单权限
        orderService.validateOrderPermission(orderId, userId, "BUYER");

        // 执行支付
        OrderDTO orderDTO = orderService.payOrder(userId, request);

        // 构建支付结果
        Map<String, Object> paymentResult = new HashMap<>();
        paymentResult.put("success", true);
        paymentResult.put("order", orderDTO);
        paymentResult.put("transactionId", "MOCK_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        paymentResult.put("paymentTime", orderDTO.getPaymentTime());
        paymentResult.put("message", "模拟支付成功");

        return Result.success("支付成功", paymentResult);
    }

    /**
     * 模拟支付回调（第三方支付平台回调）
     */
    @PostMapping("/callback")
    @Operation(summary = "模拟支付回调", description = "模拟第三方支付平台回调接口")
    public Result<String> paymentCallback(
            @RequestBody Map<String, Object> callbackData,
            HttpServletRequest httpRequest) {
        log.info("收到模拟支付回调: {}", callbackData);

        // 验证回调签名（模拟）
        String sign = (String) callbackData.get("sign");
        if (!"MOCK_SIGNATURE".equals(sign)) {
            log.warn("支付回调签名验证失败: {}", callbackData);
            return Result.paramError("签名验证失败");
        }

        // 解析回调参数
        String orderNo = (String) callbackData.get("orderNo");
        String transactionId = (String) callbackData.get("transactionId");
        String paymentStatus = (String) callbackData.get("paymentStatus");
        String paymentAmount = (String) callbackData.get("paymentAmount");

        log.info("处理支付回调: orderNo={}, transactionId={}, status={}, amount={}",
                orderNo, transactionId, paymentStatus, paymentAmount);

        // 这里应该根据回调结果更新订单状态
        // 由于是模拟环境，直接返回成功

        return Result.success("SUCCESS");
    }

    /**
     * 模拟支付结果查询
     */
    @GetMapping("/{orderId}/result")
    @Operation(summary = "查询模拟支付结果", description = "查询模拟支付结果")
    public Result<Map<String, Object>> queryPaymentResult(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("查询模拟支付结果: orderId={}, userId={}", orderId, userId);

        // 验证订单权限
        orderService.validateOrderPermission(orderId, userId, "BUYER");

        // 获取订单信息
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        // 构建支付结果
        Map<String, Object> paymentResult = new HashMap<>();
        paymentResult.put("orderId", orderId);
        paymentResult.put("orderNo", orderDTO.getOrderNo());
        paymentResult.put("paymentStatus", orderDTO.getPaymentStatus());
        paymentResult.put("paymentStatusDesc", orderDTO.getPaymentStatusDesc());
        paymentResult.put("paymentMethod", orderDTO.getPaymentMethod());
        paymentResult.put("paymentMethodDesc", orderDTO.getPaymentMethodDesc());
        paymentResult.put("paymentTime", orderDTO.getPaymentTime());
        paymentResult.put("payAmount", orderDTO.getPayAmount());

        // 模拟支付详细信息
        if (orderDTO.getPaymentStatus() == 1) { // 支付成功
            paymentResult.put("transactionId", "MOCK_TX_" + orderDTO.getOrderNo());
            paymentResult.put("paymentChannel", "MOCK_CHANNEL");
            paymentResult.put("paymentComplete", true);
        }

        return Result.success(paymentResult);
    }

    /**
     * 模拟退款
     */
    @PostMapping("/{orderId}/refund")
    @Operation(summary = "模拟退款", description = "模拟退款操作")
    public Result<Map<String, Object>> mockRefund(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @RequestParam(required = false) String refundReason,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("模拟退款: orderId={}, userId={}, reason={}", orderId, userId, refundReason);

        // 验证订单权限
        orderService.validateOrderPermission(orderId, userId, "BUYER");

        // 检查订单是否可以退款
        if (!orderService.canApplyRefund(orderId)) {
            return Result.paramError("订单当前状态不支持退款");
        }

        // 获取订单信息
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        // 模拟退款逻辑
        Map<String, Object> refundResult = new HashMap<>();
        refundResult.put("success", true);
        refundResult.put("orderId", orderId);
        refundResult.put("orderNo", orderDTO.getOrderNo());
        refundResult.put("refundAmount", orderDTO.getPayAmount());
        refundResult.put("refundReason", refundReason != null ? refundReason : "模拟退款");
        refundResult.put("refundId", "MOCK_REFUND_" + System.currentTimeMillis());
        refundResult.put("refundTime", System.currentTimeMillis());
        refundResult.put("message", "模拟退款成功，退款金额将在1-3个工作日内退回原支付账户");

        log.info("模拟退款完成: {}", refundResult);

        return Result.success("退款申请已提交", refundResult);
    }

    /**
     * 模拟支付方式列表
     */
    @GetMapping("/methods")
    @Operation(summary = "获取模拟支付方式列表", description = "获取可用的模拟支付方式列表")
    public Result<Map<String, Object>> getPaymentMethods() {
        log.debug("获取模拟支付方式列表");

        Map<String, Object> methods = new HashMap<>();

        // 支付宝模拟
        Map<String, Object> alipay = new HashMap<>();
        alipay.put("name", "支付宝");
        alipay.put("code", "ALIPAY");
        alipay.put("icon", "alipay.png");
        alipay.put("description", "支付宝模拟支付");
        alipay.put("mockAccount", "test@alipay.com");
        alipay.put("mockPassword", "123456");
        methods.put("ALIPAY", alipay);

        // 微信支付模拟
        Map<String, Object> wechat = new HashMap<>();
        wechat.put("name", "微信支付");
        wechat.put("code", "WECHAT");
        wechat.put("icon", "wechat.png");
        wechat.put("description", "微信支付模拟");
        wechat.put("mockAccount", "test@wechat.com");
        wechat.put("mockPassword", "123456");
        methods.put("WECHAT", wechat);

        // 现金支付模拟
        Map<String, Object> cash = new HashMap<>();
        cash.put("name", "现金支付");
        cash.put("code", "CASH");
        cash.put("icon", "cash.png");
        cash.put("description", "线下现金支付");
        cash.put("mockAccount", "无需账户");
        cash.put("mockPassword", "无需密码");
        methods.put("CASH", cash);

        Map<String, Object> result = new HashMap<>();
        result.put("methods", methods);
        result.put("defaultMethod", "ALIPAY");
        result.put("mockMode", true);

        return Result.success(result);
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
}