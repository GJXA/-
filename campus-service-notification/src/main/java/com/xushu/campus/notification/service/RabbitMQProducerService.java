package com.xushu.campus.notification.service;

import com.xushu.campus.notification.config.RabbitMQConfig;
import com.xushu.campus.notification.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ消息生产者服务
 */
@Slf4j
@Service
public class RabbitMQProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送通知消息到队列
     */
    public void sendNotification(NotificationDTO notification) {
        log.info("发送通知消息到队列，通知ID: {}, 类型: {}", notification.getId(), notification.getType());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                notification
        );
    }

    /**
     * 发送邮件通知消息
     */
    public void sendEmailNotification(NotificationDTO notification) {
        log.info("发送邮件通知消息，通知ID: {}, 收件人: {}", notification.getId(), notification.getReceiverEmail());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                notification
        );
    }

    /**
     * 发送短信通知消息
     */
    public void sendSmsNotification(NotificationDTO notification) {
        log.info("发送短信通知消息，通知ID: {}, 收件人手机: {}", notification.getId(), notification.getReceiverPhone());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.SMS_ROUTING_KEY,
                notification
        );
    }

    /**
     * 发送WebSocket通知消息
     */
    public void sendWebSocketNotification(NotificationDTO notification) {
        log.info("发送WebSocket通知消息，通知ID: {}, 用户ID: {}", notification.getId(), notification.getUserId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.WEBSOCKET_ROUTING_KEY,
                notification
        );
    }

    /**
     * 发送即时通知消息（根据通知渠道选择队列）
     */
    public void sendNotificationByChannel(NotificationDTO notification) {
        String channel = notification.getChannel();
        if (channel == null) {
            channel = "IN_APP";
        }

        switch (channel) {
            case "EMAIL":
                sendEmailNotification(notification);
                break;
            case "SMS":
                sendSmsNotification(notification);
                break;
            case "WEBSOCKET":
                sendWebSocketNotification(notification);
                break;
            case "IN_APP":
            default:
                sendNotification(notification);
                break;
        }
    }

    /**
     * 发送延迟消息
     */
    public void sendDelayedNotification(NotificationDTO notification, long delayMillis) {
        log.info("发送延迟通知消息，通知ID: {}, 延迟: {}ms", notification.getId(), delayMillis);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                notification,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", delayMillis);
                    return message;
                }
        );
    }

    /**
     * 批量发送通知消息
     */
    public void batchSendNotifications(java.util.List<NotificationDTO> notifications) {
        log.info("批量发送通知消息，数量: {}", notifications.size());

        for (NotificationDTO notification : notifications) {
            sendNotification(notification);
        }
    }

    /**
     * 发送自定义消息
     */
    public void sendCustomMessage(String exchange, String routingKey, Object message) {
        log.info("发送自定义消息，交换机: {}, 路由键: {}", exchange, routingKey);

        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}