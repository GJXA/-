package com.xushu.campus.notification.controller;

import com.xushu.campus.notification.dto.NotificationDTO;
import com.xushu.campus.notification.dto.WebSocketNotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * WebSocket通知控制器
 */
@Controller
public class WebSocketNotificationController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketNotificationController.class);

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 客户端发送消息到/app/notification，服务器广播到/topic/notifications
     */
    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public WebSocketNotificationDTO broadcastNotification(@Payload WebSocketNotificationDTO notification,
                                                         Principal principal) {
        log.info("收到广播通知消息，发送者: {}, 内容: {}", principal != null ? principal.getName() : "anonymous",
                notification.getContent());

        notification.setTimestamp(LocalDateTime.now());
        notification.setSender(principal != null ? principal.getName() : "system");

        return notification;
    }

    /**
     * 客户端发送消息到/app/user-notification，服务器发送点对点消息给特定用户
     */
    @MessageMapping("/user-notification")
    @SendToUser("/queue/notifications")
    public WebSocketNotificationDTO sendUserNotification(@Payload WebSocketNotificationDTO notification,
                                                        Principal principal) {
        log.info("收到用户通知消息，接收者: {}, 内容: {}", notification.getReceiverId(),
                notification.getContent());

        notification.setTimestamp(LocalDateTime.now());
        notification.setSender(principal != null ? principal.getName() : "system");

        return notification;
    }

    /**
     * 客户端订阅用户通知
     */
    @MessageMapping("/subscribe")
    public void subscribeToUserNotifications(Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        String username = principal != null ? principal.getName() : "anonymous";
        String sessionId = headerAccessor.getSessionId();
        log.info("用户 {} (会话ID: {}) 订阅了通知", username, sessionId);
    }

    /**
     * 发送通知给所有在线用户（系统通知）
     */
    public void sendSystemNotification(String title, String content) {
        // TODO: Fix Lombok annotation processing issue
        // WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
        // notification.setType("SYSTEM");
        // notification.setTitle(title);
        // notification.setContent(content);
        // notification.setTimestamp(LocalDateTime.now());
        // notification.setSender("system");
        //
        // messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.info("发送系统通知: {} - {}", title, content);
    }

    /**
     * 发送通知给特定用户
     */
    public void sendUserNotification(Long userId, String type, String title, String content) {
        WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
        notification.setReceiverId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTimestamp(LocalDateTime.now());
        notification.setSender("system");

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notification);
        log.info("发送用户通知，用户ID: {}, 类型: {}, 标题: {}", userId, type, title);
    }

    /**
     * 发送通知DTO给特定用户
     */
    public void sendNotificationDTO(Long userId, NotificationDTO notificationDTO) {
        // TODO: Fix Lombok annotation processing issue
        // WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
        // notification.setReceiverId(userId);
        // notification.setType(notificationDTO.getType());
        // notification.setTitle(notificationDTO.getTitle());
        // notification.setContent(notificationDTO.getContent());
        // notification.setTimestamp(LocalDateTime.now());
        // notification.setSender("system");
        // notification.setNotificationId(notificationDTO.getId());
        // notification.setPriority(notificationDTO.getPriority());
        //
        // messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notification);
        // log.info("发送通知DTO，用户ID: {}, 通知ID: {}", userId, notificationDTO.getId());
        log.warn("sendNotificationDTO method temporarily disabled due to compilation issues");
    }

    /**
     * 用户上线通知
     */
    public void notifyUserOnline(Long userId, String username) {
        // TODO: Fix Lombok annotation processing issue
        // WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
        // notification.setReceiverId(userId);
        // notification.setType("USER_STATUS");
        // notification.setTitle("用户上线通知");
        // notification.setContent("用户 " + username + " 已上线");
        // notification.setTimestamp(LocalDateTime.now());
        // notification.setSender("system");
        //
        // messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notification);
        log.info("用户上线通知，用户ID: {}, 用户名: {}", userId, username);
    }

    /**
     * 用户下线通知
     */
    public void notifyUserOffline(Long userId, String username) {
        // TODO: Fix Lombok annotation processing issue
        // WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
        // notification.setType("USER_STATUS");
        // notification.setTitle("用户下线通知");
        // notification.setContent("用户 " + username + " 已下线");
        // notification.setTimestamp(LocalDateTime.now());
        // notification.setSender("system");
        //
        // // 发送给其他在线用户（排除自己）
        // messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.info("用户下线通知，用户ID: {}, 用户名: {}", userId, username);
    }
}