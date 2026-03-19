package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 发送消息请求DTO
 */
@Data
@Schema(description = "发送消息请求")
public class SendMessageRequest {

    @Schema(description = "会话ID", required = true, example = "1")
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @Schema(description = "消息类型：TEXT-文本，IMAGE-图片，FILE-文件，VOICE-语音，VIDEO-视频，SYSTEM-系统消息", required = true, example = "TEXT")
    @NotBlank(message = "消息类型不能为空")
    private String messageType;

    @Schema(description = "消息内容", required = true, example = "你好！")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    @Schema(description = "附件URL", example = "https://example.com/file.pdf")
    private String attachmentUrl;

    @Schema(description = "附件名称", example = "文档.pdf")
    private String attachmentName;

    @Schema(description = "附件大小（字节）", example = "102400")
    private Long attachmentSize;

    @Schema(description = "父消息ID（用于回复）", example = "123")
    private Long parentMessageId;

    @Schema(description = "引用消息ID", example = "456")
    private Long referencedMessageId;

    @Schema(description = "是否立即发送（如果为false，则保存为草稿）", example = "true")
    private Boolean sendImmediately = true;

    @Schema(description = "是否需要消息已读回执", example = "false")
    private Boolean needReadReceipt = false;

    @Schema(description = "是否需要反应功能", example = "true")
    private Boolean allowReactions = true;

    /**
     * 验证请求参数
     */
    public void validate() {
        if (conversationId == null || conversationId <= 0) {
            throw new IllegalArgumentException("会话ID无效");
        }
        if (messageType == null || messageType.trim().isEmpty()) {
            throw new IllegalArgumentException("消息类型不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
            if (attachmentName == null || attachmentName.trim().isEmpty()) {
                throw new IllegalArgumentException("附件URL存在时必须提供附件名称");
            }
        }
    }

    /**
     * 检查是否为文本消息
     */
    public boolean isText() {
        return "TEXT".equals(messageType);
    }

    /**
     * 检查是否为图片消息
     */
    public boolean isImage() {
        return "IMAGE".equals(messageType);
    }

    /**
     * 检查是否为文件消息
     */
    public boolean isFile() {
        return "FILE".equals(messageType);
    }

    /**
     * 检查是否为语音消息
     */
    public boolean isVoice() {
        return "VOICE".equals(messageType);
    }

    /**
     * 检查是否为视频消息
     */
    public boolean isVideo() {
        return "VIDEO".equals(messageType);
    }

    /**
     * 检查是否为系统消息
     */
    public boolean isSystem() {
        return "SYSTEM".equals(messageType);
    }

    /**
     * 检查是否为回复消息
     */
    public boolean isReply() {
        return parentMessageId != null && parentMessageId > 0;
    }

    /**
     * 检查是否为引用消息
     */
    public boolean isReferenced() {
        return referencedMessageId != null && referencedMessageId > 0;
    }

    /**
     * 检查是否有附件
     */
    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.isEmpty();
    }

    /**
     * 检查是否为草稿
     */
    public boolean isDraft() {
        return Boolean.FALSE.equals(sendImmediately);
    }
}