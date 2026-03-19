package com.xushu.campus.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话实体类
 */
@Data
@TableName("conversations")
public class Conversation {

    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话类型：PRIVATE-私聊，GROUP-群聊，SYSTEM-系统
     */
    private String conversationType;

    /**
     * 会话标题（群聊时使用）
     */
    private String title;

    /**
     * 会话头像
     */
    private String avatarUrl;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 最后一条消息ID
     */
    private Long lastMessageId;

    /**
     * 最后一条消息内容（冗余）
     */
    private String lastMessageContent;

    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 成员数量
     */
    private Integer memberCount;

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

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;
}