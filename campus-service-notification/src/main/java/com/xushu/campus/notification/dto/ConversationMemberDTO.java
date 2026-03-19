package com.xushu.campus.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话成员信息DTO
 */
@Data
@Schema(description = "会话成员信息")
public class ConversationMemberDTO {

    @Schema(description = "成员关系ID")
    private Long id;

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

    @Schema(description = "用户角色：OWNER-拥有者，ADMIN-管理员，MEMBER-成员")
    private String userRole;

    @Schema(description = "用户角色描述")
    private String userRoleDesc;

    @Schema(description = "用户在会话中的昵称")
    private String nicknameInConversation;

    @Schema(description = "是否静音：0-否，1-是")
    private Integer isMuted;

    @Schema(description = "最后读取的消息ID")
    private Long lastReadMessageId;

    @Schema(description = "最后读取的消息内容")
    private String lastReadMessageContent;

    @Schema(description = "最后读取的消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastReadMessageTime;

    @Schema(description = "未读消息数（针对该成员）")
    private Integer unreadCount;

    @Schema(description = "加入时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTime;

    @Schema(description = "离开时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveTime;

    @Schema(description = "是否已离开")
    private Boolean hasLeft;

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
        if (this.userRole != null) {
            switch (this.userRole) {
                case "OWNER":
                    this.userRoleDesc = "拥有者";
                    break;
                case "ADMIN":
                    this.userRoleDesc = "管理员";
                    break;
                case "MEMBER":
                    this.userRoleDesc = "成员";
                    break;
                default:
                    this.userRoleDesc = this.userRole;
            }
        }

        this.hasLeft = this.leaveTime != null;
    }

    /**
     * 获取显示昵称
     */
    public String getDisplayNickname() {
        if (this.nicknameInConversation != null && !this.nicknameInConversation.isEmpty()) {
            return this.nicknameInConversation;
        }
        return this.userName;
    }

    /**
     * 检查是否为拥有者
     */
    public boolean isOwner() {
        return "OWNER".equals(this.userRole);
    }

    /**
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.userRole);
    }

    /**
     * 检查是否为普通成员
     */
    public boolean isMember() {
        return "MEMBER".equals(this.userRole);
    }

    /**
     * 检查是否静音
     */
    public boolean isMuted() {
        return this.isMuted != null && this.isMuted == 1;
    }

    /**
     * 检查是否已离开
     */
    public boolean hasLeft() {
        return this.hasLeft != null && this.hasLeft;
    }

    /**
     * 检查是否有未读消息
     */
    public boolean hasUnread() {
        return this.unreadCount != null && this.unreadCount > 0;
    }

    /**
     * 检查是否为活跃成员（未离开）
     */
    public boolean isActive() {
        return !hasLeft();
    }
}