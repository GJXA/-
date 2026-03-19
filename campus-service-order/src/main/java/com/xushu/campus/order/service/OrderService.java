package com.xushu.campus.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.order.dto.*;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    OrderDTO createOrder(Long userId, CreateOrderRequest request) throws BusinessException;

    /**
     * 取消订单
     */
    OrderDTO cancelOrder(Long userId, CancelOrderRequest request) throws BusinessException;

    /**
     * 支付订单
     */
    OrderDTO payOrder(Long userId, PayOrderRequest request) throws BusinessException;

    /**
     * 发货
     */
    OrderDTO deliverOrder(Long sellerId, DeliveryOrderRequest request) throws BusinessException;

    /**
     * 确认收货
     */
    OrderDTO confirmOrder(Long userId, ConfirmOrderRequest request) throws BusinessException;

    /**
     * 申请退款
     */
    OrderDTO applyRefund(Long userId, RefundRequest request) throws BusinessException;

    /**
     * 处理退款（管理员或卖家）
     */
    OrderDTO processRefund(Long operatorId, Long orderId, Boolean approve, String remark) throws BusinessException;

    /**
     * 根据ID获取订单详情
     */
    OrderDTO getOrderById(Long orderId) throws BusinessException;

    /**
     * 根据订单号获取订单详情
     */
    OrderDTO getOrderByOrderNo(String orderNo) throws BusinessException;

    /**
     * 获取用户订单列表（分页）
     */
    IPage<OrderDTO> getUserOrders(Long userId, Integer page, Integer size);

    /**
     * 获取用户订单列表（根据状态筛选）
     */
    IPage<OrderDTO> getUserOrdersByStatus(Long userId, Integer orderStatus, Integer page, Integer size);

    /**
     * 获取卖家订单列表（分页）
     */
    IPage<OrderDTO> getSellerOrders(Long sellerId, Integer page, Integer size);

    /**
     * 获取卖家订单列表（根据状态筛选）
     */
    IPage<OrderDTO> getSellerOrdersByStatus(Long sellerId, Integer orderStatus, Integer page, Integer size);

    /**
     * 搜索订单（管理员）
     */
    IPage<OrderDTO> searchOrders(OrderSearchRequest request) throws BusinessException;

    /**
     * 获取订单统计信息
     */
    OrderStatisticsDTO getOrderStatistics(Long userId) throws BusinessException;

    /**
     * 获取卖家订单统计信息
     */
    OrderStatisticsDTO getSellerOrderStatistics(Long sellerId) throws BusinessException;

    /**
     * 检查订单是否可以取消
     */
    boolean canCancelOrder(Long orderId) throws BusinessException;

    /**
     * 检查订单是否可以支付
     */
    boolean canPayOrder(Long orderId) throws BusinessException;

    /**
     * 检查订单是否可以发货
     */
    boolean canDeliverOrder(Long orderId) throws BusinessException;

    /**
     * 检查订单是否可以确认收货
     */
    boolean canConfirmOrder(Long orderId) throws BusinessException;

    /**
     * 检查订单是否可以申请退款
     */
    boolean canApplyRefund(Long orderId) throws BusinessException;

    /**
     * 取消超时未支付订单（定时任务）
     */
    void cancelExpiredOrders();

    /**
     * 自动确认收货（定时任务）
     */
    void autoConfirmOrders();

    /**
     * 更新订单过期时间
     */
    void updateOrderExpireTime(Long orderId) throws BusinessException;

    /**
     * 验证订单权限（用户只能操作自己的订单，卖家只能操作自己的商品订单）
     */
    void validateOrderPermission(Long orderId, Long userId, String role) throws BusinessException;
}