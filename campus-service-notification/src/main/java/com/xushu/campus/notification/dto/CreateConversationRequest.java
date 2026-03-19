package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建会话请求DTO
 */
@Data
@Schema(description = "创建会话请求")
public class CreateConversationRequest {

    @Schema(description = "会话类型：PRIVATE-私聊，GROUP-群聊，SYSTEM-系统", required = true, example = "PRIVATE")
    @NotBlank(message = "会话类型不能为空")
    private String conversationType;

    @Schema(description = "会话标题（群聊时使用）", example = "项目讨论群")
    private String title;

    @Schema(description = "会话头像", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "初始成员用户ID列表（不包括创建者自己）", required = true)
    @NotNull(message = "成员列表不能为空")
    private List<Long> memberUserIds;

    @Schema(description = "是否需要发送欢迎消息", example = "true")
    private Boolean sendWelcomeMessage = true;

    @Schema(description = "欢迎消息内容", example = "欢迎加入会话！")
    private String welcomeMessageContent;

    /**
     * 验证请求参数
     */
    public void validate() {
        if (conversationType == null || conversationType.trim().isEmpty()) {
            throw new IllegalArgumentException("会话类型不能为空");
        }
        if (memberUserIds == null || memberUserIds.isEmpty()) {
            throw new IllegalArgumentException("成员列表不能为空");
        }
        if ("PRIVATE".equals(conversationType) && memberUserIds.size() != 1) {
            throw new IllegalArgumentException("私聊会话只能有一个成员");
        }
    }

    /**
     * 检查是否为私聊
     */
    public boolean isPrivate() {
        return "PRIVATE".equals(conversationType);
    }

    /**
     * 检查是否为群聊
     */
    public boolean isGroup() {
        return "GROUP".equals(conversationType);
    }

    /**
     * 检查是否为系统会话
     */
    public boolean isSystem() {
        return "SYSTEM".equals(conversationType);
    }
}