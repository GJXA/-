package com.xushu.campus.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.order.constant.OrderConstants;
import com.xushu.campus.order.dto.*;
import com.xushu.campus.order.entity.Order;
import com.xushu.campus.order.mapper.OrderMapper;
import com.xushu.campus.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(Long userId, CreateOrderRequest request) {
        log.info("创建订单: userId={}, productId={}", userId, request.getProductId());

        // 1. 验证商品信息（简化版本，暂时跳过Feign调用）
        // TODO: 实际项目中需要通过Feign调用商品服务获取商品信息
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(request.getProductId());
        productDTO.setUserId(999L); // 临时占位，实际应从商品服务获取
        productDTO.setPrice(request.getProductPrice() != null ? request.getProductPrice() : new BigDecimal("100.00"));
        productDTO.setStatus(1); // 假设商品已上架
        productDTO.setTitle(request.getProductTitle() != null ? request.getProductTitle() : "临时商品");

        // 商品验证
        if (!productDTO.getStatus().equals(1)) { // 商品状态：1-上架中
            throw BusinessException.paramError("商品未上架或已下架");
        }

        // 2. 验证商品价格（防止前端篡改）
        if (request.getProductPrice() != null && request.getProductPrice().compareTo(productDTO.getPrice()) != 0) {
            log.warn("商品价格不一致: requestPrice={}, actualPrice={}", request.getProductPrice(), productDTO.getPrice());
            throw BusinessException.paramError("商品价格已变更，请刷新后重试");
        }

        // 3. 验证用户不能购买自己的商品
        if (productDTO.getUserId().equals(userId)) {
            throw BusinessException.paramError("不能购买自己发布的商品");
        }

        // 4. 生成订单号
        String orderNo = generateOrderNo();

        // 5. 计算订单金额
        BigDecimal totalAmount = productDTO.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal payAmount = totalAmount; // 暂时没有优惠

        // 6. 验证支付方式
        if (!OrderConstants.PaymentMethod.isValid(request.getPaymentMethod())) {
            throw BusinessException.paramError("不支持的支付方式");
        }

        // 7. 创建订单实体
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(request.getProductId());
        order.setProductTitle(productDTO.getTitle());
        order.setProductPrice(productDTO.getPrice());
        order.setProductImage(request.getProductImage());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(OrderConstants.PaymentStatus.PENDING);
        order.setOrderStatus(OrderConstants.OrderStatus.PENDING_PAYMENT);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setBuyerMessage(request.getBuyerMessage());
        order.setSellerId(productDTO.getUserId());
        order.setBuyerNickname(getUserNickname(userId));
        order.setSellerNickname(getUserNickname(productDTO.getUserId()));
        order.setExpireTime(LocalDateTime.now().plusMinutes(OrderConstants.Default.AUTO_CANCEL_MINUTES));
        order.setDeleted(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setVersion(0);

        // 8. 保存订单
        orderMapper.insert(order);

        // 9. 更新商品状态（可选：标记为已售出或减少库存）
        // productServiceClient.updateProductStatus(productDTO.getId(), OrderConstants.ProductStatus.SOLD);

        // 10. 清除相关缓存
        clearOrderCache(order.getId(), userId, productDTO.getUserId());

        // 11. 返回订单DTO
        return convertToDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO cancelOrder(Long userId, CancelOrderRequest request) {
        log.info("取消订单: userId={}, orderId={}", userId, request.getOrderId());

        // 1. 获取订单
        Order order = getOrderByIdWithCheck(request.getOrderId());

        // 2. 验证权限
        validateOrderPermission(order.getId(), userId, "BUYER");

        // 3. 检查是否可以取消
        if (!canCancelOrder(order.getId())) {
            throw BusinessException.paramError("当前订单状态不支持取消");
        }

        // 4. 更新订单状态
        order.setOrderStatus(OrderConstants.OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(request.getCancelReason());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);

        // 5. 清除缓存
        clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());

        // 6. 返回更新后的订单
        return convertToDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO payOrder(Long userId, PayOrderRequest request) {
        log.info("支付订单: userId={}, orderId={}", userId, request.getOrderId());

        // 1. 获取订单
        Order order = getOrderByIdWithCheck(request.getOrderId());

        // 2. 验证权限
        validateOrderPermission(order.getId(), userId, "BUYER");

        // 3. 检查是否可以支付
        if (!canPayOrder(order.getId())) {
            throw BusinessException.paramError("当前订单状态不支持支付");
        }

        // 4. 验证支付方式
        if (!request.getPaymentMethod().equals(order.getPaymentMethod())) {
            throw BusinessException.paramError("支付方式与订单不一致");
        }

        // 5. 检查订单是否已过期
        if (order.getExpireTime() != null && order.getExpireTime().isBefore(LocalDateTime.now())) {
            throw BusinessException.paramError("订单已过期，请重新下单");
        }

        // 6. 模拟支付过程
        boolean paymentSuccess = simulatePayment(request);
        if (!paymentSuccess) {
            throw BusinessException.paramError("支付失败，请重试");
        }

        // 7. 更新订单状态
        order.setPaymentStatus(OrderConstants.PaymentStatus.SUCCESS);
        order.setOrderStatus(OrderConstants.OrderStatus.PENDING_DELIVERY);
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);

        // 8. 清除缓存
        clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());

        // 9. 返回更新后的订单
        return convertToDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO deliverOrder(Long sellerId, DeliveryOrderRequest request) {
        log.info("发货订单: sellerId={}, orderId={}", sellerId, request.getOrderId());

        // 1. 获取订单
        Order order = getOrderByIdWithCheck(request.getOrderId());

        // 2. 验证权限（卖家）
        if (!order.getSellerId().equals(sellerId)) {
            throw BusinessException.forbidden("无权操作此订单");
        }

        // 3. 检查是否可以发货
        if (!canDeliverOrder(order.getId())) {
            throw BusinessException.paramError("当前订单状态不支持发货");
        }

        // 4. 更新订单状态
        order.setOrderStatus(OrderConstants.OrderStatus.PENDING_RECEIPT);
        order.setDeliveryTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 这里可以添加发货信息到order扩展表中
        // order.setExpressCompany(request.getExpressCompany());
        // order.setExpressNumber(request.getExpressNumber());

        orderMapper.updateById(order);

        // 5. 清除缓存
        clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());

        // 6. 返回更新后的订单
        return convertToDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO confirmOrder(Long userId, ConfirmOrderRequest request) {
        log.info("确认收货: userId={}, orderId={}", userId, request.getOrderId());

        // 1. 获取订单
        Order order = getOrderByIdWithCheck(request.getOrderId());

        // 2. 验证权限
        validateOrderPermission(order.getId(), userId, "BUYER");

        // 3. 检查是否可以确认收货
        if (!canConfirmOrder(order.getId())) {
            throw BusinessException.paramError("当前订单状态不支持确认收货");
        }

        // 4. 更新订单状态
        order.setOrderStatus(OrderConstants.OrderStatus.COMPLETED);
        order.setConfirmTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);

        // 5. 清除缓存
        clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());

        // 6. 返回更新后的订单
        return convertToDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO applyRefund(Long userId, RefundRequest request) {
        log.info("申请退款: userId={}, orderId={}", userId, request.getOrderId());

        // 1. 获取订单
        Order order = getOrderByIdWithCheck(request.getOrderId());

        // 2. 验证权限
        validateOrderPermission(order.getId(), userId, "BUYER");

        // 3. 检查是否可以申请退款
        if (!canApplyRefund(order.getId())) {
            throw BusinessException.paramError("当前订单状态不支持退款");
        }

        // 4. 验证退款金额
        if (request.getRefundAmount().compareTo(order.getPayAmount()) > 0) {
            throw BusinessException.paramError("退款金额不能超过支付金额");
        }

        // 5. 更新订单状态（这里简化为直接标记为已退款，实际应该有退款流程）
        order.setPaymentStatus(OrderConstants.PaymentStatus.REFUNDED);
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);

        // 6. 清除缓存
        clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());

        // 7. 返回更新后的订单
        return convertToDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO processRefund(Long operatorId, Long orderId, Boolean approve, String remark) {
        log.info("处理退款: operatorId={}, orderId={}, approve={}", operatorId, orderId, approve);

        // 1. 获取订单
        Order order = getOrderByIdWithCheck(orderId);

        // 2. 验证操作权限（卖家或管理员）
        boolean isSeller = order.getSellerId().equals(operatorId);
        boolean isAdmin = checkIsAdmin(operatorId);

        if (!isSeller && !isAdmin) {
            throw BusinessException.forbidden("无权处理此退款");
        }

        // 3. 检查订单状态是否在退款中（这里简化，实际应该有退款申请状态）
        if (order.getPaymentStatus() != OrderConstants.PaymentStatus.REFUNDED) {
            throw BusinessException.paramError("订单未申请退款");
        }

        // 4. 处理退款
        if (approve) {
            // 批准退款，执行退款操作
            boolean refundSuccess = simulateRefund(order);
            if (!refundSuccess) {
                throw BusinessException.paramError("退款处理失败");
            }
            // 可以添加退款处理记录
        } else {
            // 拒绝退款，恢复订单状态
            order.setPaymentStatus(OrderConstants.PaymentStatus.SUCCESS);
            order.setUpdateTime(LocalDateTime.now());
        }

        orderMapper.updateById(order);

        // 5. 清除缓存
        clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());

        // 6. 返回更新后的订单
        return convertToDTO(order);
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        log.debug("获取订单详情: orderId={}", orderId);

        // 1. 尝试从缓存获取
        String cacheKey = OrderConstants.CacheKey.ORDER_DETAIL + orderId;
        OrderDTO cachedOrder = (OrderDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedOrder != null) {
            return cachedOrder;
        }

        // 2. 从数据库获取
        Order order = getOrderByIdWithCheck(orderId);

        // 3. 转换为DTO
        OrderDTO orderDTO = convertToDTO(order);

        // 4. 缓存结果（5分钟）
        redisTemplate.opsForValue().set(cacheKey, orderDTO, 5, TimeUnit.MINUTES);

        return orderDTO;
    }

    @Override
    public OrderDTO getOrderByOrderNo(String orderNo) {
        log.debug("根据订单号获取订单: orderNo={}", orderNo);

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw BusinessException.notFound("订单不存在");
        }

        return convertToDTO(order);
    }

    @Override
    public IPage<OrderDTO> getUserOrders(Long userId, Integer page, Integer size) {
        log.debug("获取用户订单列表: userId={}, page={}, size={}", userId, page, size);

        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> orderPage = orderMapper.selectByUserId(pageParam, userId);

        // 转换为DTO
        return orderPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<OrderDTO> getUserOrdersByStatus(Long userId, Integer orderStatus, Integer page, Integer size) {
        log.debug("获取用户订单列表(按状态): userId={}, status={}, page={}, size={}", userId, orderStatus, page, size);

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                .eq(Order::getDeleted, 0)
                .eq(Order::getOrderStatus, orderStatus)
                .orderByDesc(Order::getCreateTime);

        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> orderPage = orderMapper.selectPage(pageParam, queryWrapper);

        return orderPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<OrderDTO> getSellerOrders(Long sellerId, Integer page, Integer size) {
        log.debug("获取卖家订单列表: sellerId={}, page={}, size={}", sellerId, page, size);

        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> orderPage = orderMapper.selectBySellerId(pageParam, sellerId);

        return orderPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<OrderDTO> getSellerOrdersByStatus(Long sellerId, Integer orderStatus, Integer page, Integer size) {
        log.debug("获取卖家订单列表(按状态): sellerId={}, status={}, page={}, size={}", sellerId, orderStatus, page, size);

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getSellerId, sellerId)
                .eq(Order::getDeleted, 0)
                .eq(Order::getOrderStatus, orderStatus)
                .orderByDesc(Order::getCreateTime);

        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> orderPage = orderMapper.selectPage(pageParam, queryWrapper);

        return orderPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<OrderDTO> searchOrders(OrderSearchRequest request) {
        log.debug("搜索订单: {}", request);

        Page<Order> pageParam = new Page<>(request.getPage(), request.getSize());
        IPage<Order> orderPage = orderMapper.searchOrders(pageParam, request);

        return orderPage.convert(this::convertToDTO);
    }

    @Override
    public OrderStatisticsDTO getOrderStatistics(Long userId) {
        log.debug("获取用户订单统计: userId={}", userId);

        OrderStatisticsDTO statistics = new OrderStatisticsDTO();

        // 这里简化实现，实际应该使用SQL聚合查询
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                .eq(Order::getDeleted, 0);

        List<Order> orders = orderMapper.selectList(queryWrapper);

        statistics.setPendingPaymentCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.PENDING_PAYMENT)
                .count());
        statistics.setPendingDeliveryCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.PENDING_DELIVERY)
                .count());
        statistics.setPendingReceiptCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.PENDING_RECEIPT)
                .count());
        statistics.setCompletedCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.COMPLETED)
                .count());
        statistics.setCancelledCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.CANCELLED ||
                        o.getOrderStatus() == OrderConstants.OrderStatus.CLOSED)
                .count());

        statistics.setTotalAmount(orders.stream()
                .filter(o -> o.getPaymentStatus() == OrderConstants.PaymentStatus.SUCCESS)
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        statistics.calculateTotalCount();

        return statistics;
    }

    @Override
    public OrderStatisticsDTO getSellerOrderStatistics(Long sellerId) {
        log.debug("获取卖家订单统计: sellerId={}", sellerId);

        OrderStatisticsDTO statistics = new OrderStatisticsDTO();

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getSellerId, sellerId)
                .eq(Order::getDeleted, 0);

        List<Order> orders = orderMapper.selectList(queryWrapper);

        statistics.setPendingPaymentCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.PENDING_PAYMENT)
                .count());
        statistics.setPendingDeliveryCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.PENDING_DELIVERY)
                .count());
        statistics.setPendingReceiptCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.PENDING_RECEIPT)
                .count());
        statistics.setCompletedCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.COMPLETED)
                .count());
        statistics.setCancelledCount((int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderConstants.OrderStatus.CANCELLED ||
                        o.getOrderStatus() == OrderConstants.OrderStatus.CLOSED)
                .count());

        statistics.setTotalAmount(orders.stream()
                .filter(o -> o.getPaymentStatus() == OrderConstants.PaymentStatus.SUCCESS)
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        statistics.calculateTotalCount();

        return statistics;
    }

    @Override
    public boolean canCancelOrder(Long orderId) {
        Order order = getOrderByIdWithCheck(orderId);
        return OrderConstants.OrderStatus.canCancel(order.getOrderStatus());
    }

    @Override
    public boolean canPayOrder(Long orderId) {
        Order order = getOrderByIdWithCheck(orderId);
        return order.getOrderStatus() == OrderConstants.OrderStatus.PENDING_PAYMENT &&
                order.getPaymentStatus() == OrderConstants.PaymentStatus.PENDING;
    }

    @Override
    public boolean canDeliverOrder(Long orderId) {
        Order order = getOrderByIdWithCheck(orderId);
        return order.getOrderStatus() == OrderConstants.OrderStatus.PENDING_DELIVERY &&
                order.getPaymentStatus() == OrderConstants.PaymentStatus.SUCCESS;
    }

    @Override
    public boolean canConfirmOrder(Long orderId) {
        Order order = getOrderByIdWithCheck(orderId);
        return order.getOrderStatus() == OrderConstants.OrderStatus.PENDING_RECEIPT &&
                order.getPaymentStatus() == OrderConstants.PaymentStatus.SUCCESS;
    }

    @Override
    public boolean canApplyRefund(Long orderId) {
        Order order = getOrderByIdWithCheck(orderId);
        return order.getOrderStatus() == OrderConstants.OrderStatus.COMPLETED &&
                order.getPaymentStatus() == OrderConstants.PaymentStatus.SUCCESS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelExpiredOrders() {
        log.info("开始取消超时未支付订单");

        LocalDateTime now = LocalDateTime.now();
        List<Order> expiredOrders = orderMapper.selectExpiredOrders(now);

        for (Order order : expiredOrders) {
            try {
                log.info("取消超时订单: orderId={}, orderNo={}", order.getId(), order.getOrderNo());
                order.setOrderStatus(OrderConstants.OrderStatus.CANCELLED);
                order.setCancelTime(now);
                order.setCancelReason("超时未支付，自动取消");
                order.setUpdateTime(now);
                orderMapper.updateById(order);
                clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());
            } catch (Exception e) {
                log.error("取消超时订单失败: orderId={}", order.getId(), e);
            }
        }

        log.info("完成取消超时未支付订单，共处理{}个订单", expiredOrders.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoConfirmOrders() {
        log.info("开始自动确认收货");

        LocalDateTime deadline = LocalDateTime.now().minusDays(OrderConstants.Default.AUTO_CONFIRM_DAYS);
        List<Order> autoConfirmOrders = orderMapper.selectAutoConfirmOrders(deadline);

        for (Order order : autoConfirmOrders) {
            try {
                log.info("自动确认收货: orderId={}, orderNo={}", order.getId(), order.getOrderNo());
                order.setOrderStatus(OrderConstants.OrderStatus.COMPLETED);
                order.setConfirmTime(LocalDateTime.now());
                order.setUpdateTime(LocalDateTime.now());
                orderMapper.updateById(order);
                clearOrderCache(order.getId(), order.getUserId(), order.getSellerId());
            } catch (Exception e) {
                log.error("自动确认收货失败: orderId={}", order.getId(), e);
            }
        }

        log.info("完成自动确认收货，共处理{}个订单", autoConfirmOrders.size());
    }

    @Override
    public void updateOrderExpireTime(Long orderId) {
        log.debug("更新订单过期时间: orderId={}", orderId);

        Order order = getOrderByIdWithCheck(orderId);
        order.setExpireTime(LocalDateTime.now().plusMinutes(OrderConstants.Default.AUTO_CANCEL_MINUTES));
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);
    }

    @Override
    public void validateOrderPermission(Long orderId, Long userId, String role) {
        Order order = getOrderByIdWithCheck(orderId);

        if ("BUYER".equals(role)) {
            if (!order.getUserId().equals(userId)) {
                throw BusinessException.forbidden("无权操作此订单");
            }
        } else if ("SELLER".equals(role)) {
            if (!order.getSellerId().equals(userId)) {
                throw BusinessException.forbidden("无权操作此订单");
            }
        } else {
            throw BusinessException.paramError("无效的角色类型");
        }
    }

    /**
     * 内部辅助方法
     */

    private Order getOrderByIdWithCheck(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw BusinessException.notFound("订单不存在");
        }
        return order;
    }

    private String generateOrderNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return OrderConstants.Default.ORDER_NO_PREFIX + timestamp.substring(timestamp.length() - 8) + random;
    }

    private String getUserNickname(Long userId) {
        try {
            // 调用用户服务获取用户昵称
            // 这里简化，实际应该调用UserServiceClient
            return "用户" + userId;
        } catch (Exception e) {
            log.warn("获取用户昵称失败: userId={}", userId, e);
            return "用户" + userId;
        }
    }

    private boolean simulatePayment(PayOrderRequest request) {
        // 模拟支付过程
        // 实际项目中应该调用第三方支付接口
        log.info("模拟支付: orderId={}, paymentMethod={}", request.getOrderId(), request.getPaymentMethod());

        // 简单的支付验证
        if ("CASH".equals(request.getPaymentMethod())) {
            // 现金支付直接成功
            return true;
        } else if ("ALIPAY".equals(request.getPaymentMethod()) || "WECHAT".equals(request.getPaymentMethod())) {
            // 模拟第三方支付，这里直接返回成功
            return true;
        }

        return false;
    }

    private boolean simulateRefund(Order order) {
        // 模拟退款过程
        log.info("模拟退款: orderId={}, orderNo={}, refundAmount={}", order.getId(), order.getOrderNo(), order.getPayAmount());

        // 实际项目中应该调用第三方支付退款接口
        return true;
    }

    private boolean checkIsAdmin(Long userId) {
        // 检查用户是否是管理员
        // 这里简化，实际应该调用用户服务检查角色
        return false;
    }

    private OrderDTO convertToDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        orderDTO.calculateDesc(); // 计算状态描述

        return orderDTO;
    }

    private void clearOrderCache(Long orderId, Long userId, Long sellerId) {
        // 清除订单详情缓存
        String orderDetailKey = OrderConstants.CacheKey.ORDER_DETAIL + orderId;
        redisTemplate.delete(orderDetailKey);

        // 清除用户订单列表缓存
        String userOrdersKey = OrderConstants.CacheKey.USER_ORDERS + userId;
        redisTemplate.delete(userOrdersKey);

        // 清除卖家订单列表缓存
        String sellerOrdersKey = OrderConstants.CacheKey.USER_ORDERS + sellerId;
        redisTemplate.delete(sellerOrdersKey);

        log.debug("清除订单缓存: orderId={}", orderId);
    }

    /**
     * 简化版的商品DTO（用于测试，实际项目应从商品服务获取）
     */
    private static class ProductDTO {
        private Long id;
        private Long userId;
        private String title;
        private BigDecimal price;
        private Integer status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}