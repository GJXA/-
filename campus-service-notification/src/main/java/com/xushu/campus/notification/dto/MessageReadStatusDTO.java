package com.xushu.campus.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息阅读状态信息DTO
 */
@Data
@Schema(description = "消息阅读状态信息")
public class MessageReadStatusDTO {

    @Schema(description = "阅读状态ID")
    private Long id;

    @Schema(description = "消息ID")
    private Long messageId;

    @Schema(description = "消息内容")
    private String messageContent;

    @Schema(description = "消息内容摘要")
    private String messageContentSummary;

    @Schema(description = "消息发送者ID")
    private Long messageSenderId;

    @Schema(description = "消息发送者名称")
    private String messageSenderName;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 计算描述信息
     */
    public void calculateDesc() {
        if (this.messageContent != null && !this.messageContent.isEmpty()) {
            this.messageContentSummary = this.messageContent.length() > 100 ?
                this.messageContent.substring(0, 100) + "..." : this.messageContent;
        }
    }

    /**
     * 检查阅读时间是否在最近24小时内
     */
    public boolean isReadRecently() {
        if (this.readTime == null) {
            return false;
        }
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return this.readTime.isAfter(twentyFourHoursAgo);
    }

    /**
     * 检查阅读时间是否在最近一小时内
     */
    public boolean isReadWithinHour() {
        if (this.readTime == null) {
            return false;
        }
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return this.readTime.isAfter(oneHourAgo);
    }

    /**
     * 获取阅读时间描述（如"刚刚"、"1小时前"等）
     */
    public String getReadTimeDesc() {
        if (this.readTime == null) {
            return "未知";
        }

        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(this.readTime, now).getSeconds();

        if (seconds < 60) {
            return "刚刚";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "分钟前";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + "小时前";
        } else {
            long days = seconds / 86400;
            return days + "天前";
        }
    }
}