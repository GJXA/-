package com.xushu.campus.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话信息DTO
 */
@Data
@Schema(description = "聊天会话信息")
public class ConversationDTO {

    @Schema(description = "会话ID")
    private Long id;

    @Schema(description = "会话类型：PRIVATE-私聊，GROUP-群聊，SYSTEM-系统")
    private String conversationType;

    @Schema(description = "会话类型描述")
    private String conversationTypeDesc;

    @Schema(description = "会话标题（群聊时使用）")
    private String title;

    @Schema(description = "会话头像")
    private String avatarUrl;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "创建者名称")
    private String creatorName;

    @Schema(description = "最后一条消息ID")
    private Long lastMessageId;

    @Schema(description = "最后一条消息内容（冗余）")
    private String lastMessageContent;

    @Schema(description = "最后一条消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;

    @Schema(description = "最后一条消息发送者ID")
    private Long lastMessageSenderId;

    @Schema(description = "最后一条消息发送者名称")
    private String lastMessageSenderName;

    @Schema(description = "未读消息数")
    private Integer unreadCount;

    @Schema(description = "成员数量")
    private Integer memberCount;

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
        if (this.conversationType != null) {
            // 这里可以添加类型描述的逻辑
            switch (this.conversationType) {
                case "PRIVATE":
                    this.conversationTypeDesc = "私聊";
                    break;
                case "GROUP":
                    this.conversationTypeDesc = "群聊";
                    break;
                case "SYSTEM":
                    this.conversationTypeDesc = "系统";
                    break;
                default:
                    this.conversationTypeDesc = this.conversationType;
            }
        }
    }

    /**
     * 检查是否私聊
     */
    public boolean isPrivate() {
        return "PRIVATE".equals(this.conversationType);
    }

    /**
     * 检查是否群聊
     */
    public boolean isGroup() {
        return "GROUP".equals(this.conversationType);
    }

    /**
     * 检查是否系统会话
     */
    public boolean isSystem() {
        return "SYSTEM".equals(this.conversationType);
    }

    /**
     * 检查是否有未读消息
     */
    public boolean hasUnread() {
        return this.unreadCount != null && this.unreadCount > 0;
    }

    /**
     * 获取会话显示标题
     */
    public String getDisplayTitle() {
        if (this.title != null && !this.title.isEmpty()) {
            return this.title;
        }
        // 如果是私聊，可以显示对方名称，这里简化处理
        return "会话";
    }
}