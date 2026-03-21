package com.xushu.campus.notification.service;

import com.xushu.campus.notification.config.RabbitMQConfig;
import com.xushu.campus.notification.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ消息消费者服务
 */
@Slf4j
@Service
public class RabbitMQConsumerService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.xushu.campus.notification.controller.WebSocketNotificationController webSocketNotificationController;

    /**
     * 消费通知消息
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consumeNotification(NotificationDTO notification) {
        log.info("消费通知消息，通知ID: {}, 标题: {}", notification.getId(), notification.getTitle());

        try {
            // 处理通知（例如：保存到数据库，更新状态等）
            // 这里可以调用notificationService的方法
            log.info("处理通知消息成功，通知ID: {}", notification.getId());
        } catch (Exception e) {
            log.error("处理通知消息失败，通知ID: {}", notification.getId(), e);
            // 可以根据业务需求进行重试或记录错误
        }
    }

    /**
     * 消费邮件通知消息
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailNotification(NotificationDTO notification) {
        log.info("消费邮件通知消息，通知ID: {}, 收件人: {}", notification.getId(), notification.getReceiverEmail());

        try {
            // 发送邮件
            if (notification.getReceiverEmail() != null && !notification.getReceiverEmail().isEmpty()) {
                emailService.sendHtmlEmail(
                        notification.getReceiverEmail(),
                        notification.getTitle(),
                        notification.getContent()
                );
                log.info("邮件发送成功，通知ID: {}, 收件人: {}", notification.getId(), notification.getReceiverEmail());
            } else {
                log.warn("邮件地址为空，跳过发送，通知ID: {}", notification.getId());
            }
        } catch (Exception e) {
            log.error("发送邮件失败，通知ID: {}, 收件人: {}", notification.getId(), notification.getReceiverEmail(), e);
            // 可以记录失败，后续进行重试
        }
    }

    /**
     * 消费短信通知消息
     */
    @RabbitListener(queues = RabbitMQConfig.SMS_QUEUE)
    public void consumeSmsNotification(NotificationDTO notification) {
        log.info("消费短信通知消息，通知ID: {}, 收件人手机: {}", notification.getId(), notification.getReceiverPhone());

        try {
            // 这里实现短信发送逻辑
            // 由于短信服务通常需要第三方API，这里只是示例
            log.info("模拟发送短信成功，通知ID: {}, 手机号: {}", notification.getId(), notification.getReceiverPhone());
        } catch (Exception e) {
            log.error("发送短信失败，通知ID: {}, 手机号: {}", notification.getId(), notification.getReceiverPhone(), e);
        }
    }

    /**
     * 消费WebSocket通知消息
     */
    @RabbitListener(queues = RabbitMQConfig.WEBSOCKET_QUEUE)
    public void consumeWebSocketNotification(NotificationDTO notification) {
        log.info("消费WebSocket通知消息，通知ID: {}, 用户ID: {}", notification.getId(), notification.getUserId());

        try {
            // 通过WebSocket发送通知
            if (notification.getUserId() != null) {
                webSocketNotificationController.sendNotificationDTO(
                        notification.getUserId(),
                        notification
                );
                log.info("WebSocket通知发送成功，通知ID: {}, 用户ID: {}", notification.getId(), notification.getUserId());
            } else {
                log.warn("用户ID为空，跳过WebSocket发送，通知ID: {}", notification.getId());
            }
        } catch (Exception e) {
            log.error("WebSocket通知发送失败，通知ID: {}, 用户ID: {}", notification.getId(), notification.getUserId(), e);
        }
    }

    /**
     * 处理死信队列消息（需要配置死信队列）
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_DLQ)
    public void consumeDeadLetterNotification(NotificationDTO notification) {
        log.warn("消费死信队列消息，通知ID: {}, 标题: {}", notification.getId(), notification.getTitle());

        // 处理死信消息：记录日志、发送告警、人工干预等
        // 这里可以发送告警邮件或记录到数据库
        log.error("死信消息需要处理，通知ID: {}, 标题: {}", notification.getId(), notification.getTitle());
    }

    /**
     * 处理邮件死信队列消息
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_DLQ)
    public void consumeDeadLetterEmail(NotificationDTO notification) {
        log.warn("消费邮件死信队列消息，通知ID: {}, 收件人: {}", notification.getId(), notification.getReceiverEmail());

        // 处理失败的邮件消息
        log.error("邮件发送失败，需要人工干预，通知ID: {}, 收件人: {}", notification.getId(), notification.getReceiverEmail());
    }

    /**
     * 处理短信死信队列消息
     */
    @RabbitListener(queues = RabbitMQConfig.SMS_DLQ)
    public void consumeDeadLetterSms(NotificationDTO notification) {
        log.warn("消费短信死信队列消息，通知ID: {}, 收件人手机: {}", notification.getId(), notification.getReceiverPhone());

        // 处理失败的短信消息
        log.error("短信发送失败，需要人工干预，通知ID: {}, 手机号: {}", notification.getId(), notification.getReceiverPhone());
    }

    /**
     * 处理WebSocket死信队列消息
     */
    @RabbitListener(queues = RabbitMQConfig.WEBSOCKET_DLQ)
    public void consumeDeadLetterWebSocket(NotificationDTO notification) {
        log.warn("消费WebSocket死信队列消息，通知ID: {}, 用户ID: {}", notification.getId(), notification.getUserId());

        // 处理失败的WebSocket消息
        log.error("WebSocket通知发送失败，需要人工干预，通知ID: {}, 用户ID: {}", notification.getId(), notification.getUserId());
    }

    /**
     * 批量消费消息（如果需要）
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE, concurrency = "5")
    public void consumeNotificationBatch(java.util.List<NotificationDTO> notifications) {
        log.info("批量消费通知消息，数量: {}", notifications.size());

        for (NotificationDTO notification : notifications) {
            consumeNotification(notification);
        }
    }
}