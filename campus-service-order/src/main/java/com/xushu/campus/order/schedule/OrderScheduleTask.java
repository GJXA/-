package com.xushu.campus.order.schedule;

import com.xushu.campus.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduleTask {

    private final OrderService orderService;

    /**
     * 取消超时未支付订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "${campus.order.schedule.cancel-expired-orders:0 */5 * * * ?}")
    public void cancelExpiredOrdersTask() {
        try {
            log.info("开始执行取消超时订单定时任务");
            long startTime = System.currentTimeMillis();

            orderService.cancelExpiredOrders();

            long endTime = System.currentTimeMillis();
            log.info("完成取消超时订单定时任务，耗时{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("取消超时订单定时任务执行失败", e);
        }
    }

    /**
     * 自动确认收货
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "${campus.order.schedule.auto-confirm-orders:0 0 2 * * ?}")
    public void autoConfirmOrdersTask() {
        try {
            log.info("开始执行自动确认收货定时任务");
            long startTime = System.currentTimeMillis();

            orderService.autoConfirmOrders();

            long endTime = System.currentTimeMillis();
            log.info("完成自动确认收货定时任务，耗时{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("自动确认收货定时任务执行失败", e);
        }
    }

    /**
     * 订单统计定时任务（示例）
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void orderStatisticsTask() {
        try {
            log.debug("开始执行订单统计定时任务");
            // 这里可以添加订单统计逻辑，如生成日报、周报等
            // 例如：统计今日订单数量、金额等
            log.debug("完成订单统计定时任务");
        } catch (Exception e) {
            log.error("订单统计定时任务执行失败", e);
        }
    }

    /**
     * 支付状态同步任务（示例）
     * 每10分钟执行一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void paymentStatusSyncTask() {
        try {
            log.debug("开始执行支付状态同步定时任务");
            // 这里可以添加支付状态同步逻辑
            // 例如：查询第三方支付平台，同步支付状态
            log.debug("完成支付状态同步定时任务");
        } catch (Exception e) {
            log.error("支付状态同步定时任务执行失败", e);
        }
    }
}