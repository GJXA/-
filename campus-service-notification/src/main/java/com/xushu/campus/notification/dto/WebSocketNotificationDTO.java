package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * WebSocket通知DTO
 */
@Schema(description = "WebSocket通知信息")
public class WebSocketNotificationDTO {

    @Schema(description = "通知ID")
    private Long notificationId;

    @Schema(description = "接收者用户ID（为null表示广播）")
    private Long receiverId;

    @Schema(description = "通知类型：SYSTEM-系统通知，ORDER-订单通知，JOB-兼职通知，PRODUCT-商品通知，USER-用户通知，USER_STATUS-用户状态通知")
    private String type;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "通知优先级：0-低，1-普通，2-高，3-紧急")
    private Integer priority;

    @Schema(description = "发送者")
    private String sender;

    @Schema(description = "发送时间")
    private LocalDateTime timestamp;

    @Schema(description = "关联的业务ID")
    private Long relatedId;

    @Schema(description = "关联的业务类型")
    private String relatedType;

    @Schema(description = "业务数据（JSON格式）")
    private String businessData;

    @Schema(description = "是否需要确认")
    private boolean requiresAck = false;

    @Schema(description = "确认状态：false-未确认，true-已确认")
    private boolean acknowledged = false;

    @Schema(description = "确认时间")
    private LocalDateTime ackTime;

    @Schema(description = "消息ID（用于确认）")
    private String messageId;

    // Getters and Setters
    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }

    public String getRelatedType() { return relatedType; }
    public void setRelatedType(String relatedType) { this.relatedType = relatedType; }

    public String getBusinessData() { return businessData; }
    public void setBusinessData(String businessData) { this.businessData = businessData; }

    public boolean isRequiresAck() { return requiresAck; }
    public void setRequiresAck(boolean requiresAck) { this.requiresAck = requiresAck; }

    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }

    public LocalDateTime getAckTime() { return ackTime; }
    public void setAckTime(LocalDateTime ackTime) { this.ackTime = ackTime; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    /**
     * 创建系统通知
     */
    public static WebSocketNotificationDTO createSystemNotification(String title, String content) {
        WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
        notification.setType("SYSTEM");
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSender("system");
        notification.setTimestamp(LocalDateTime.now());
        return notification;
    }

    /**
     * 创建用户通知
     */
    public static WebSocketNotificationDTO createUserNotification(Long receiverId, String type,
                                                                 String title, String content) {
        WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
        notification.setReceiverId(receiverId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSender("system");
        notification.setTimestamp(LocalDateTime.now());
        return notification;
    }

    /**
     * 检查是否为广播消息
     */
    public boolean isBroadcast() {
        return receiverId == null;
    }

    /**
     * 检查是否为系统消息
     */
    public boolean isSystemMessage() {
        return "SYSTEM".equals(type);
    }

    /**
     * 检查是否为用户状态消息
     */
    public boolean isUserStatusMessage() {
        return "USER_STATUS".equals(type);
    }
}