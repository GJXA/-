package com.xushu.campus.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xushu.campus.notification.constant.NotificationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知信息DTO
 */
@Data
@Schema(description = "通知信息")
public class NotificationDTO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "通知类型")
    private String type;

    @Schema(description = "通知类型描述")
    private String typeDesc;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "通知状态")
    private Integer status;

    @Schema(description = "通知状态描述")
    private String statusDesc;

    @Schema(description = "通知优先级")
    private Integer priority;

    @Schema(description = "通知优先级描述")
    private String priorityDesc;

    @Schema(description = "发送方式")
    private String channel;

    @Schema(description = "发送方式描述")
    private String channelDesc;

    @Schema(description = "关联的业务ID")
    private Long relatedId;

    @Schema(description = "关联的业务类型")
    private String relatedType;

    @Schema(description = "业务数据（JSON格式）")
    private String businessData;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    @Schema(description = "读取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "邮件发送状态")
    private Integer emailStatus;

    @Schema(description = "短信发送状态")
    private Integer smsStatus;

    @Schema(description = "WebSocket发送状态")
    private Integer websocketStatus;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "发送人名称")
    private String senderName;

    @Schema(description = "接收人邮箱")
    private String receiverEmail;

    @Schema(description = "接收人手机号")
    private String receiverPhone;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 计算描述信息
     */
    public void calculateDesc() {
        if (this.type != null) {
            this.typeDesc = NotificationConstants.NotificationType.getDesc(this.type);
        }
        if (this.status != null) {
            this.statusDesc = NotificationConstants.NotificationStatus.getDesc(this.status);
        }
        if (this.priority != null) {
            this.priorityDesc = NotificationConstants.NotificationPriority.getDesc(this.priority);
        }
        if (this.channel != null) {
            this.channelDesc = NotificationConstants.NotificationChannel.getDesc(this.channel);
        }
    }

    /**
     * 检查是否未读
     */
    public boolean isUnread() {
        return this.status != null && this.status == NotificationConstants.NotificationStatus.UNREAD;
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        if (this.expireTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.expireTime);
    }

    /**
     * 检查是否可以标记为已读
     */
    public boolean canMarkAsRead() {
        return NotificationConstants.NotificationStatus.canMarkAsRead(this.status);
    }

    /**
     * 检查是否可以删除
     */
    public boolean canDelete() {
        return NotificationConstants.NotificationStatus.canDelete(this.status);
    }

    /**
     * 获取业务数据的简要信息
     */
    public String getBusinessSummary() {
        if (this.businessData == null || this.businessData.isEmpty()) {
            return "";
        }
        // 这里可以解析JSON并提取关键信息，简化实现返回原始数据
        return this.businessData.length() > 100 ? this.businessData.substring(0, 100) + "..." : this.businessData;
    }
}