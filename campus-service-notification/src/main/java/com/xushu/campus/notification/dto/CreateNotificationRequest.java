package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 创建通知请求
 */
@Data
@Schema(description = "创建通知请求")
public class CreateNotificationRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotBlank(message = "通知类型不能为空")
    @Schema(description = "通知类型：SYSTEM-系统通知，ORDER-订单通知，JOB-兼职通知，PRODUCT-商品通知，USER-用户通知", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotBlank(message = "通知标题不能为空")
    @Schema(description = "通知标题", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 200)
    private String title;

    @NotBlank(message = "通知内容不能为空")
    @Schema(description = "通知内容", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 2000)
    private String content;

    @Schema(description = "通知优先级：0-低，1-普通，2-高，3-紧急", defaultValue = "1")
    private Integer priority = 1;

    @Schema(description = "发送方式：IN_APP-站内消息，EMAIL-电子邮件，SMS-短信，WEBSOCKET-WebSocket", defaultValue = "IN_APP")
    private String channel = "IN_APP";

    @Schema(description = "关联的业务ID")
    private Long relatedId;

    @Schema(description = "关联的业务类型")
    private String relatedType;

    @Schema(description = "业务数据（JSON格式）")
    private String businessData;

    @Schema(description = "发送时间（为空则立即发送）")
    private LocalDateTime sendTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "发送人ID（系统通知时为0）")
    private Long senderId;

    @Schema(description = "发送人名称")
    private String senderName;

    @Schema(description = "接收人邮箱（如果发送方式包含EMAIL）")
    private String receiverEmail;

    @Schema(description = "接收人手机号（如果发送方式包含SMS）")
    private String receiverPhone;

    /**
     * 验证通知类型是否有效
     */
    public boolean isValidType() {
        return com.xushu.campus.notification.constant.NotificationConstants.NotificationType.isValid(this.type);
    }

    /**
     * 验证发送方式是否有效
     */
    public boolean isValidChannel() {
        return com.xushu.campus.notification.constant.NotificationConstants.NotificationChannel.isValid(this.channel);
    }

    /**
     * 验证优先级是否有效
     */
    public boolean isValidPriority() {
        return com.xushu.campus.notification.constant.NotificationConstants.NotificationPriority.isValid(this.priority);
    }

    /**
     * 检查是否需要发送邮件
     */
    public boolean needSendEmail() {
        return "EMAIL".equals(this.channel) || (this.receiverEmail != null && !this.receiverEmail.isEmpty());
    }

    /**
     * 检查是否需要发送短信
     */
    public boolean needSendSms() {
        return "SMS".equals(this.channel) || (this.receiverPhone != null && !this.receiverPhone.isEmpty());
    }

    /**
     * 检查是否需要发送WebSocket
     */
    public boolean needSendWebSocket() {
        return "WEBSOCKET".equals(this.channel);
    }

    /**
     * 验证请求数据是否有效
     */
    public boolean isValid() {
        return this.userId != null && this.userId > 0 &&
                this.type != null && this.isValidType() &&
                this.title != null && !this.title.trim().isEmpty() &&
                this.content != null && !this.content.trim().isEmpty() &&
                this.isValidPriority() &&
                this.isValidChannel();
    }
}