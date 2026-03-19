package com.xushu.campus.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息信息DTO
 */
@Data
@Schema(description = "聊天消息信息")
public class MessageDTO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "发送者ID")
    private Long senderId;

    @Schema(description = "发送者名称")
    private String senderName;

    @Schema(description = "发送者头像")
    private String senderAvatar;

    @Schema(description = "消息类型：TEXT-文本，IMAGE-图片，FILE-文件，VOICE-语音，VIDEO-视频，SYSTEM-系统消息")
    private String messageType;

    @Schema(description = "消息类型描述")
    private String messageTypeDesc;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息内容摘要（用于列表显示）")
    private String contentSummary;

    @Schema(description = "附件URL")
    private String attachmentUrl;

    @Schema(description = "附件名称")
    private String attachmentName;

    @Schema(description = "附件大小（字节）")
    private Long attachmentSize;

    @Schema(description = "附件大小格式化")
    private String attachmentSizeFormatted;

    @Schema(description = "是否已编辑：0-否，1-是")
    private Integer isEdited;

    @Schema(description = "编辑时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editTime;

    @Schema(description = "是否已撤回：0-否，1-是")
    private Integer isRecalled;

    @Schema(description = "撤回时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recallTime;

    @Schema(description = "已读人数")
    private Integer readCount;

    @Schema(description = "反应数量")
    private Integer reactionCount;

    @Schema(description = "父消息ID（用于回复）")
    private Long parentMessageId;

    @Schema(description = "父消息内容")
    private String parentMessageContent;

    @Schema(description = "父消息发送者")
    private String parentMessageSender;

    @Schema(description = "引用消息ID")
    private Long referencedMessageId;

    @Schema(description = "引用消息内容")
    private String referencedMessageContent;

    @Schema(description = "引用消息发送者")
    private String referencedMessageSender;

    @Schema(description = "是否已读（针对当前用户）")
    private Boolean isRead;

    @Schema(description = "当前用户反应类型")
    private String currentUserReactionType;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 计算描述信息和格式化字段
     */
    public void calculateDesc() {
        if (this.messageType != null) {
            switch (this.messageType) {
                case "TEXT":
                    this.messageTypeDesc = "文本";
                    break;
                case "IMAGE":
                    this.messageTypeDesc = "图片";
                    break;
                case "FILE":
                    this.messageTypeDesc = "文件";
                    break;
                case "VOICE":
                    this.messageTypeDesc = "语音";
                    break;
                case "VIDEO":
                    this.messageTypeDesc = "视频";
                    break;
                case "SYSTEM":
                    this.messageTypeDesc = "系统消息";
                    break;
                default:
                    this.messageTypeDesc = this.messageType;
            }
        }

        if (this.attachmentSize != null) {
            this.attachmentSizeFormatted = formatFileSize(this.attachmentSize);
        }

        // 如果内容摘要为空，根据内容生成
        if (this.contentSummary == null || this.contentSummary.isEmpty()) {
            if (this.content != null && !this.content.isEmpty()) {
                this.contentSummary = this.content.length() > 50 ? this.content.substring(0, 50) + "..." : this.content;
            }
        }
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 检查是否为文本消息
     */
    public boolean isText() {
        return "TEXT".equals(this.messageType);
    }

    /**
     * 检查是否为图片消息
     */
    public boolean isImage() {
        return "IMAGE".equals(this.messageType);
    }

    /**
     * 检查是否为文件消息
     */
    public boolean isFile() {
        return "FILE".equals(this.messageType);
    }

    /**
     * 检查是否为语音消息
     */
    public boolean isVoice() {
        return "VOICE".equals(this.messageType);
    }

    /**
     * 检查是否为视频消息
     */
    public boolean isVideo() {
        return "VIDEO".equals(this.messageType);
    }

    /**
     * 检查是否为系统消息
     */
    public boolean isSystem() {
        return "SYSTEM".equals(this.messageType);
    }

    /**
     * 检查是否已编辑
     */
    public boolean isEdited() {
        return this.isEdited != null && this.isEdited == 1;
    }

    /**
     * 检查是否已撤回
     */
    public boolean isRecalled() {
        return this.isRecalled != null && this.isRecalled == 1;
    }

    /**
     * 检查是否有附件
     */
    public boolean hasAttachment() {
        return this.attachmentUrl != null && !this.attachmentUrl.isEmpty();
    }

    /**
     * 检查是否为回复消息
     */
    public boolean isReply() {
        return this.parentMessageId != null;
    }

    /**
     * 检查是否为引用消息
     */
    public boolean isReferenced() {
        return this.referencedMessageId != null;
    }

    /**
     * 获取显示内容（如果是撤回消息，显示"消息已撤回"）
     */
    public String getDisplayContent() {
        if (this.isRecalled()) {
            return "消息已撤回";
        }
        return this.content;
    }
}