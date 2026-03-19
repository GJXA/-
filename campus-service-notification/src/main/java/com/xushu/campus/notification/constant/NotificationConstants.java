package com.xushu.campus.notification.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 通知相关常量
 */
public class NotificationConstants {

    /**
     * 通知类型
     */
    public static class NotificationType {
        /**
         * 系统通知
         */
        public static final String SYSTEM = "SYSTEM";

        /**
         * 订单通知
         */
        public static final String ORDER = "ORDER";

        /**
         * 兼职通知
         */
        public static final String JOB = "JOB";

        /**
         * 商品通知
         */
        public static final String PRODUCT = "PRODUCT";

        /**
         * 用户通知
         */
        public static final String USER = "USER";

        /**
         * 验证通知类型是否有效
         */
        public static boolean isValid(String type) {
            return type != null && Arrays.asList(SYSTEM, ORDER, JOB, PRODUCT, USER).contains(type);
        }

        /**
         * 获取所有通知类型
         */
        public static List<String> getAllTypes() {
            return Arrays.asList(SYSTEM, ORDER, JOB, PRODUCT, USER);
        }

        /**
         * 获取通知类型描述
         */
        public static String getDesc(String type) {
            if (type == null) {
                return "未知";
            }
            switch (type) {
                case SYSTEM:
                    return "系统通知";
                case ORDER:
                    return "订单通知";
                case JOB:
                    return "兼职通知";
                case PRODUCT:
                    return "商品通知";
                case USER:
                    return "用户通知";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 通知状态
     */
    public static class NotificationStatus {
        /**
         * 未读
         */
        public static final int UNREAD = 0;

        /**
         * 已读
         */
        public static final int READ = 1;

        /**
         * 已删除
         */
        public static final int DELETED = 2;

        /**
         * 验证状态是否有效
         */
        public static boolean isValid(Integer status) {
            return status != null && (status == UNREAD || status == READ || status == DELETED);
        }

        /**
         * 获取状态描述
         */
        public static String getDesc(Integer status) {
            if (status == null) {
                return "未知";
            }
            switch (status) {
                case UNREAD:
                    return "未读";
                case READ:
                    return "已读";
                case DELETED:
                    return "已删除";
                default:
                    return "未知";
            }
        }

        /**
         * 检查是否可以标记为已读
         */
        public static boolean canMarkAsRead(Integer status) {
            return status != null && status == UNREAD;
        }

        /**
         * 检查是否可以删除
         */
        public static boolean canDelete(Integer status) {
            return status != null && (status == UNREAD || status == READ);
        }
    }

    /**
     * 通知优先级
     */
    public static class NotificationPriority {
        /**
         * 低优先级
         */
        public static final int LOW = 0;

        /**
         * 普通优先级
         */
        public static final int NORMAL = 1;

        /**
         * 高优先级
         */
        public static final int HIGH = 2;

        /**
         * 紧急优先级
         */
        public static final int URGENT = 3;

        /**
         * 验证优先级是否有效
         */
        public static boolean isValid(Integer priority) {
            return priority != null && priority >= LOW && priority <= URGENT;
        }

        /**
         * 获取优先级描述
         */
        public static String getDesc(Integer priority) {
            if (priority == null) {
                return "未知";
            }
            switch (priority) {
                case LOW:
                    return "低";
                case NORMAL:
                    return "普通";
                case HIGH:
                    return "高";
                case URGENT:
                    return "紧急";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 通知发送方式
     */
    public static class NotificationChannel {
        /**
         * 站内消息
         */
        public static final String IN_APP = "IN_APP";

        /**
         * 电子邮件
         */
        public static final String EMAIL = "EMAIL";

        /**
         * 短信
         */
        public static final String SMS = "SMS";

        /**
         * WebSocket
         */
        public static final String WEBSOCKET = "WEBSOCKET";

        /**
         * 验证发送方式是否有效
         */
        public static boolean isValid(String channel) {
            return channel != null && Arrays.asList(IN_APP, EMAIL, SMS, WEBSOCKET).contains(channel);
        }

        /**
         * 获取所有发送方式
         */
        public static List<String> getAllChannels() {
            return Arrays.asList(IN_APP, EMAIL, SMS, WEBSOCKET);
        }

        /**
         * 获取发送方式描述
         */
        public static String getDesc(String channel) {
            if (channel == null) {
                return "未知";
            }
            switch (channel) {
                case IN_APP:
                    return "站内消息";
                case EMAIL:
                    return "电子邮件";
                case SMS:
                    return "短信";
                case WEBSOCKET:
                    return "WebSocket";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 邮件模板类型
     */
    public static class EmailTemplate {
        /**
         * 验证码邮件
         */
        public static final String VERIFICATION_CODE = "VERIFICATION_CODE";

        /**
         * 注册成功邮件
         */
        public static final String REGISTRATION_SUCCESS = "REGISTRATION_SUCCESS";

        /**
         * 订单创建邮件
         */
        public static final String ORDER_CREATED = "ORDER_CREATED";

        /**
         * 订单支付成功邮件
         */
        public static final String ORDER_PAID = "ORDER_PAID";

        /**
         * 兼职申请提交邮件
         */
        public static final String JOB_APPLICATION_SUBMITTED = "JOB_APPLICATION_SUBMITTED";

        /**
         * 兼职申请结果邮件
         */
        public static final String JOB_APPLICATION_RESULT = "JOB_APPLICATION_RESULT";

        /**
         * 验证邮件模板是否有效
         */
        public static boolean isValid(String template) {
            return template != null && Arrays.asList(
                    VERIFICATION_CODE, REGISTRATION_SUCCESS, ORDER_CREATED, ORDER_PAID,
                    JOB_APPLICATION_SUBMITTED, JOB_APPLICATION_RESULT
            ).contains(template);
        }

        /**
         * 获取邮件模板描述
         */
        public static String getDesc(String template) {
            if (template == null) {
                return "未知";
            }
            switch (template) {
                case VERIFICATION_CODE:
                    return "验证码邮件";
                case REGISTRATION_SUCCESS:
                    return "注册成功邮件";
                case ORDER_CREATED:
                    return "订单创建邮件";
                case ORDER_PAID:
                    return "订单支付成功邮件";
                case JOB_APPLICATION_SUBMITTED:
                    return "兼职申请提交邮件";
                case JOB_APPLICATION_RESULT:
                    return "兼职申请结果邮件";
                default:
                    return "未知";
            }
        }
    }

    /**
     * WebSocket消息类型
     */
    public static class WebSocketMessageType {
        /**
         * 通知消息
         */
        public static final String NOTIFICATION = "NOTIFICATION";

        /**
         * 聊天消息
         */
        public static final String CHAT = "CHAT";

        /**
         * 系统消息
         */
        public static final String SYSTEM = "SYSTEM";

        /**
         * 验证消息类型是否有效
         */
        public static boolean isValid(String type) {
            return type != null && Arrays.asList(NOTIFICATION, CHAT, SYSTEM).contains(type);
        }

        /**
         * 获取消息类型描述
         */
        public static String getDesc(String type) {
            if (type == null) {
                return "未知";
            }
            switch (type) {
                case NOTIFICATION:
                    return "通知消息";
                case CHAT:
                    return "聊天消息";
                case SYSTEM:
                    return "系统消息";
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
         * 用户未读通知数量缓存
         */
        public static final String USER_UNREAD_COUNT = "notification:unread:user:";

        /**
         * 通知详情缓存
         */
        public static final String NOTIFICATION_DETAIL = "notification:detail:";

        /**
         * 用户通知列表缓存
         */
        public static final String USER_NOTIFICATIONS = "notification:user:";

        /**
         * 邮件发送频率限制
         */
        public static final String EMAIL_RATE_LIMIT = "notification:email:rate:";

        /**
         * 生成用户未读通知数量缓存键
         */
        public static String getUserUnreadCountKey(Long userId) {
            return USER_UNREAD_COUNT + userId;
        }

        /**
         * 生成通知详情缓存键
         */
        public static String getNotificationDetailKey(Long notificationId) {
            return NOTIFICATION_DETAIL + notificationId;
        }

        /**
         * 生成用户通知列表缓存键
         */
        public static String getUserNotificationsKey(Long userId, String type) {
            return USER_NOTIFICATIONS + userId + ":" + type;
        }

        /**
         * 生成邮件发送频率限制缓存键
         */
        public static String getEmailRateLimitKey(String email) {
            return EMAIL_RATE_LIMIT + email;
        }
    }

    /**
     * 默认配置
     */
    public static class DefaultConfig {
        /**
         * 默认保留时间（天）
         */
        public static final int RETENTION_DAYS = 30;

        /**
         * 默认批量发送大小
         */
        public static final int BATCH_SIZE = 100;

        /**
         * 默认分页大小
         */
        public static final int PAGE_SIZE = 20;

        /**
         * 邮件发送频率限制（次/分钟）
         */
        public static final int EMAIL_RATE_LIMIT = 5;

        /**
         * 最大重试次数
         */
        public static final int MAX_RETRY_COUNT = 3;

        /**
         * WebSocket端点路径
         */
        public static final String WEBSOCKET_ENDPOINT = "/ws";

        /**
         * WebSocket允许的源
         */
        public static final String WEBSOCKET_ALLOWED_ORIGINS = "*";
    }
}