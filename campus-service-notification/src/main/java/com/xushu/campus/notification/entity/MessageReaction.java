package com.xushu.campus.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息反应实体类
 */
@Data
@TableName("message_reactions")
public class MessageReaction {

    /**
     * 反应ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 反应类型：LIKE-点赞，HEART-爱心，LAUGH-大笑，SAD-难过，ANGRY-生气
     */
    private String reactionType;

    /**
     * 反应表情
     */
    private String reactionEmoji;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}