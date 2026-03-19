package com.xushu.campus.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话成员实体类
 */
@Data
@TableName("conversation_members")
public class ConversationMember {

    /**
     * 成员关系ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户角色：OWNER-拥有者，ADMIN-管理员，MEMBER-成员
     */
    private String userRole;

    /**
     * 用户在会话中的昵称
     */
    private String nicknameInConversation;

    /**
     * 是否静音：0-否，1-是
     */
    private Integer isMuted;

    /**
     * 最后读取的消息ID
     */
    private Long lastReadMessageId;

    /**
     * 未读消息数（针对该成员）
     */
    private Integer unreadCount;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 离开时间
     */
    private LocalDateTime leaveTime;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}