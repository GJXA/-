package com.xushu.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.order.dto.OrderSearchRequest;
import com.xushu.campus.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} AND deleted = 0")
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表（分页）
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Order> selectByUserId(Page<Order> page, @Param("userId") Long userId);

    /**
     * 根据卖家ID查询订单列表（分页）
     */
    @Select("SELECT * FROM orders WHERE seller_id = #{sellerId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Order> selectBySellerId(Page<Order> page, @Param("sellerId") Long sellerId);

    /**
     * 查询待支付的订单（用于定时任务取消超时订单）
     */
    @Select("SELECT * FROM orders WHERE order_status = 0 AND payment_status = 0 AND deleted = 0 AND expire_time < #{now}")
    List<Order> selectExpiredOrders(@Param("now") LocalDateTime now);

    /**
     * 查询待确认收货的订单（用于定时任务自动确认收货）
     */
    @Select("SELECT * FROM orders WHERE order_status = 2 AND payment_status = 1 AND deleted = 0 AND create_time < #{deadline}")
    List<Order> selectAutoConfirmOrders(@Param("deadline") LocalDateTime deadline);

    /**
     * 更新订单状态
     */
    @Select("UPDATE orders SET order_status = #{orderStatus}, update_time = NOW() WHERE id = #{orderId} AND deleted = 0")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("orderStatus") Integer orderStatus);

    /**
     * 更新支付状态
     */
    @Select("UPDATE orders SET payment_status = #{paymentStatus}, payment_time = #{paymentTime}, update_time = NOW() WHERE id = #{orderId} AND deleted = 0")
    int updatePaymentStatus(@Param("orderId") Long orderId,
                            @Param("paymentStatus") Integer paymentStatus,
                            @Param("paymentTime") LocalDateTime paymentTime);

    /**
     * 更新发货信息
     */
    @Select("UPDATE orders SET order_status = #{orderStatus}, delivery_time = #{deliveryTime}, update_time = NOW() WHERE id = #{orderId} AND deleted = 0")
    int updateDeliveryInfo(@Param("orderId") Long orderId,
                           @Param("orderStatus") Integer orderStatus,
                           @Param("deliveryTime") LocalDateTime deliveryTime);

    /**
     * 更新确认收货信息
     */
    @Select("UPDATE orders SET order_status = #{orderStatus}, confirm_time = #{confirmTime}, update_time = NOW() WHERE id = #{orderId} AND deleted = 0")
    int updateConfirmInfo(@Param("orderId") Long orderId,
                          @Param("orderStatus") Integer orderStatus,
                          @Param("confirmTime") LocalDateTime confirmTime);

    /**
     * 更新取消信息
     */
    @Select("UPDATE orders SET order_status = #{orderStatus}, cancel_time = #{cancelTime}, cancel_reason = #{cancelReason}, update_time = NOW() WHERE id = #{orderId} AND deleted = 0")
    int updateCancelInfo(@Param("orderId") Long orderId,
                         @Param("orderStatus") Integer orderStatus,
                         @Param("cancelTime") LocalDateTime cancelTime,
                         @Param("cancelReason") String cancelReason);

    /**
     * 根据条件搜索订单（使用XML配置）
     */
    IPage<Order> searchOrders(Page<Order> page, @Param("request") OrderSearchRequest request);

    /**
     * 查询用户的订单统计
     */
    @Select("SELECT " +
            "COUNT(CASE WHEN order_status = 0 THEN 1 END) AS pending_payment_count, " +
            "COUNT(CASE WHEN order_status = 1 THEN 1 END) AS pending_delivery_count, " +
            "COUNT(CASE WHEN order_status = 2 THEN 1 END) AS pending_receipt_count, " +
            "COUNT(CASE WHEN order_status = 3 THEN 1 END) AS completed_count, " +
            "COUNT(CASE WHEN order_status = 4 THEN 1 END) AS cancelled_count " +
            "FROM orders WHERE user_id = #{userId} AND deleted = 0")
    OrderStatistics selectUserOrderStatistics(@Param("userId") Long userId);

    /**
     * 统计类（内部使用）
     */
    class OrderStatistics {
        private Integer pendingPaymentCount;
        private Integer pendingDeliveryCount;
        private Integer pendingReceiptCount;
        private Integer completedCount;
        private Integer cancelledCount;

        // getters and setters
        public Integer getPendingPaymentCount() {
            return pendingPaymentCount;
        }

        public void setPendingPaymentCount(Integer pendingPaymentCount) {
            this.pendingPaymentCount = pendingPaymentCount;
        }

        public Integer getPendingDeliveryCount() {
            return pendingDeliveryCount;
        }

        public void setPendingDeliveryCount(Integer pendingDeliveryCount) {
            this.pendingDeliveryCount = pendingDeliveryCount;
        }

        public Integer getPendingReceiptCount() {
            return pendingReceiptCount;
        }

        public void setPendingReceiptCount(Integer pendingReceiptCount) {
            this.pendingReceiptCount = pendingReceiptCount;
        }

        public Integer getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(Integer completedCount) {
            this.completedCount = completedCount;
        }

        public Integer getCancelledCount() {
            return cancelledCount;
        }

        public void setCancelledCount(Integer cancelledCount) {
            this.cancelledCount = cancelledCount;
        }
    }
}