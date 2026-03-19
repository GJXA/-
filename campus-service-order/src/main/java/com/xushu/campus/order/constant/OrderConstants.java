package com.xushu.campus.order.constant;

/**
 * 订单相关常量
 */
public class OrderConstants {

    /**
     * 订单状态
     */
    public static class OrderStatus {
        /**
         * 待付款
         */
        public static final int PENDING_PAYMENT = 0;

        /**
         * 待发货
         */
        public static final int PENDING_DELIVERY = 1;

        /**
         * 待收货
         */
        public static final int PENDING_RECEIPT = 2;

        /**
         * 已完成
         */
        public static final int COMPLETED = 3;

        /**
         * 已取消
         */
        public static final int CANCELLED = 4;

        /**
         * 已关闭
         */
        public static final int CLOSED = 5;

        /**
         * 获取状态描述
         */
        public static String getDesc(int status) {
            switch (status) {
                case PENDING_PAYMENT:
                    return "待付款";
                case PENDING_DELIVERY:
                    return "待发货";
                case PENDING_RECEIPT:
                    return "待收货";
                case COMPLETED:
                    return "已完成";
                case CANCELLED:
                    return "已取消";
                case CLOSED:
                    return "已关闭";
                default:
                    return "未知";
            }
        }

        /**
         * 检查是否可以取消订单
         */
        public static boolean canCancel(int status) {
            return status == PENDING_PAYMENT || status == PENDING_DELIVERY;
        }

        /**
         * 检查是否可以确认收货
         */
        public static boolean canConfirmReceipt(int status) {
            return status == PENDING_RECEIPT;
        }

        /**
         * 检查是否可以发货
         */
        public static boolean canDeliver(int status) {
            return status == PENDING_DELIVERY;
        }
    }

    /**
     * 支付状态
     */
    public static class PaymentStatus {
        /**
         * 待支付
         */
        public static final int PENDING = 0;

        /**
         * 支付成功
         */
        public static final int SUCCESS = 1;

        /**
         * 支付失败
         */
        public static final int FAILED = 2;

        /**
         * 已退款
         */
        public static final int REFUNDED = 3;

        /**
         * 获取状态描述
         */
        public static String getDesc(int status) {
            switch (status) {
                case PENDING:
                    return "待支付";
                case SUCCESS:
                    return "支付成功";
                case FAILED:
                    return "支付失败";
                case REFUNDED:
                    return "已退款";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 支付方式
     */
    public static class PaymentMethod {
        /**
         * 支付宝
         */
        public static final String ALIPAY = "ALIPAY";

        /**
         * 微信支付
         */
        public static final String WECHAT = "WECHAT";

        /**
         * 现金支付
         */
        public static final String CASH = "CASH";

        /**
         * 检查支付方式是否有效
         */
        public static boolean isValid(String method) {
            return ALIPAY.equals(method) || WECHAT.equals(method) || CASH.equals(method);
        }

        /**
         * 获取支付方式描述
         */
        public static String getDesc(String method) {
            switch (method) {
                case ALIPAY:
                    return "支付宝";
                case WECHAT:
                    return "微信支付";
                case CASH:
                    return "现金支付";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 缓存键
     */
    public static class CacheKey {
        /**
         * 订单详情缓存前缀
         */
        public static final String ORDER_DETAIL = "order:detail:";

        /**
         * 用户订单列表缓存前缀
         */
        public static final String USER_ORDERS = "order:user:";

        /**
         * 订单锁定前缀（防止重复支付）
         */
        public static final String ORDER_LOCK = "order:lock:";

        /**
         * 支付订单缓存前缀
         */
        public static final String PAY_ORDER = "order:pay:";
    }

    /**
     * 默认值
     */
    public static class Default {
        /**
         * 默认分页大小
         */
        public static final int PAGE_SIZE = 20;

        /**
         * 订单号前缀
         */
        public static final String ORDER_NO_PREFIX = "CA";

        /**
         * 订单自动取消时间（分钟）
         */
        public static final int AUTO_CANCEL_MINUTES = 30;

        /**
         * 订单自动确认收货时间（天）
         */
        public static final int AUTO_CONFIRM_DAYS = 7;
    }

    /**
     * 订单操作类型
     */
    public static class Operation {
        /**
         * 创建订单
         */
        public static final String CREATE = "CREATE";

        /**
         * 取消订单
         */
        public static final String CANCEL = "CANCEL";

        /**
         * 支付订单
         */
        public static final String PAY = "PAY";

        /**
         * 发货
         */
        public static final String DELIVER = "DELIVER";

        /**
         * 确认收货
         */
        public static final String CONFIRM_RECEIPT = "CONFIRM_RECEIPT";

        /**
         * 申请退款
         */
        public static final String APPLY_REFUND = "APPLY_REFUND";

        /**
         * 处理退款
         */
        public static final String PROCESS_REFUND = "PROCESS_REFUND";

        /**
         * 获取操作描述
         */
        public static String getDesc(String operation) {
            switch (operation) {
                case CREATE:
                    return "创建订单";
                case CANCEL:
                    return "取消订单";
                case PAY:
                    return "支付订单";
                case DELIVER:
                    return "发货";
                case CONFIRM_RECEIPT:
                    return "确认收货";
                case APPLY_REFUND:
                    return "申请退款";
                case PROCESS_REFUND:
                    return "处理退款";
                default:
                    return "未知操作";
            }
        }
    }

    /**
     * 支付配置
     */
    public static class PaymentConfig {
        /**
         * 支付宝支付超时时间（分钟）
         */
        public static final int ALIPAY_TIMEOUT_MINUTES = 30;

        /**
         * 微信支付超时时间（分钟）
         */
        public static final int WECHAT_TIMEOUT_MINUTES = 30;

        /**
         * 支付回调地址
         */
        public static final String PAY_CALLBACK_URL = "/api/orders/pay/callback";
    }

}