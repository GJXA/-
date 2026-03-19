package com.xushu.campus.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息反应信息DTO
 */
@Data
@Schema(description = "消息反应信息")
public class MessageReactionDTO {

    @Schema(description = "反应ID")
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

    @Schema(description = "反应类型：LIKE-点赞，HEART-爱心，LAUGH-大笑，SAD-难过，ANGRY-生气")
    private String reactionType;

    @Schema(description = "反应类型描述")
    private String reactionTypeDesc;

    @Schema(description = "反应表情")
    private String reactionEmoji;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 计算描述信息
     */
    public void calculateDesc() {
        if (this.reactionType != null) {
            switch (this.reactionType) {
                case "LIKE":
                    this.reactionTypeDesc = "点赞";
                    this.reactionEmoji = this.reactionEmoji != null ? this.reactionEmoji : "👍";
                    break;
                case "HEART":
                    this.reactionTypeDesc = "爱心";
                    this.reactionEmoji = this.reactionEmoji != null ? this.reactionEmoji : "❤️";
                    break;
                case "LAUGH":
                    this.reactionTypeDesc = "大笑";
                    this.reactionEmoji = this.reactionEmoji != null ? this.reactionEmoji : "😄";
                    break;
                case "SAD":
                    this.reactionTypeDesc = "难过";
                    this.reactionEmoji = this.reactionEmoji != null ? this.reactionEmoji : "😢";
                    break;
                case "ANGRY":
                    this.reactionTypeDesc = "生气";
                    this.reactionEmoji = this.reactionEmoji != null ? this.reactionEmoji : "😠";
                    break;
                default:
                    this.reactionTypeDesc = this.reactionType;
                    this.reactionEmoji = this.reactionEmoji != null ? this.reactionEmoji : "👍";
            }
        }

        if (this.messageContent != null && !this.messageContent.isEmpty()) {
            this.messageContentSummary = this.messageContent.length() > 100 ?
                this.messageContent.substring(0, 100) + "..." : this.messageContent;
        }
    }

    /**
     * 检查是否为点赞
     */
    public boolean isLike() {
        return "LIKE".equals(this.reactionType);
    }

    /**
     * 检查是否为爱心
     */
    public boolean isHeart() {
        return "HEART".equals(this.reactionType);
    }

    /**
     * 检查是否为大笑
     */
    public boolean isLaugh() {
        return "LAUGH".equals(this.reactionType);
    }

    /**
     * 检查是否为难过
     */
    public boolean isSad() {
        return "SAD".equals(this.reactionType);
    }

    /**
     * 检查是否为生气
     */
    public boolean isAngry() {
        return "ANGRY".equals(this.reactionType);
    }

    /**
     * 获取反应时间描述（如"刚刚"、"1小时前"等）
     */
    public String getCreateTimeDesc() {
        if (this.createTime == null) {
            return "未知";
        }

        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(this.createTime, now).getSeconds();

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

    /**
     * 检查反应时间是否在最近24小时内
     */
    public boolean isReactedRecently() {
        if (this.createTime == null) {
            return false;
        }
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return this.createTime.isAfter(twentyFourHoursAgo);
    }

    /**
     * 获取完整反应描述（如"张三点赞了消息"）
     */
    public String getFullReactionDesc() {
        String userName = this.userName != null ? this.userName : "用户";
        String reactionDesc = this.reactionTypeDesc != null ? this.reactionTypeDesc : "反应";
        return userName + reactionDesc + "了消息";
    }
}