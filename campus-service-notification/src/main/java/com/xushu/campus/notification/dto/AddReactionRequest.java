package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 添加消息反应请求DTO
 */
@Data
@Schema(description = "添加消息反应请求")
public class AddReactionRequest {

    @Schema(description = "消息ID", required = true, example = "1")
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    @Schema(description = "反应类型：LIKE-点赞，HEART-爱心，LAUGH-大笑，SAD-难过，ANGRY-生气", required = true, example = "LIKE")
    @NotBlank(message = "反应类型不能为空")
    private String reactionType;

    @Schema(description = "反应表情（可选，如果不提供则根据反应类型自动设置）", example = "👍")
    private String reactionEmoji;

    @Schema(description = "是否替换现有反应（如果用户已对该消息有其他反应）", example = "true")
    private Boolean replaceExisting = true;

    /**
     * 验证请求参数
     */
    public void validate() {
        if (messageId == null || messageId <= 0) {
            throw new IllegalArgumentException("消息ID无效");
        }
        if (reactionType == null || reactionType.trim().isEmpty()) {
            throw new IllegalArgumentException("反应类型不能为空");
        }
    }

    /**
     * 检查是否为点赞
     */
    public boolean isLike() {
        return "LIKE".equals(reactionType);
    }

    /**
     * 检查是否为爱心
     */
    public boolean isHeart() {
        return "HEART".equals(reactionType);
    }

    /**
     * 检查是否为大笑
     */
    public boolean isLaugh() {
        return "LAUGH".equals(reactionType);
    }

    /**
     * 检查是否为难过
     */
    public boolean isSad() {
        return "SAD".equals(reactionType);
    }

    /**
     * 检查是否为生气
     */
    public boolean isAngry() {
        return "ANGRY".equals(reactionType);
    }

    /**
     * 获取默认表情
     */
    public String getDefaultEmoji() {
        if (reactionType == null) {
            return "👍";
        }
        switch (reactionType) {
            case "LIKE":
                return "👍";
            case "HEART":
                return "❤️";
            case "LAUGH":
                return "😄";
            case "SAD":
                return "😢";
            case "ANGRY":
                return "😠";
            default:
                return "👍";
        }
    }

    /**
     * 获取最终使用的表情
     */
    public String getFinalEmoji() {
        if (reactionEmoji != null && !reactionEmoji.trim().isEmpty()) {
            return reactionEmoji;
        }
        return getDefaultEmoji();
    }
}