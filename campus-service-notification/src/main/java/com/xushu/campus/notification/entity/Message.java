package com.xushu.campus.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@Data
@TableName("messages")
public class Message {

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 消息类型：TEXT-文本，IMAGE-图片，FILE-文件，VOICE-语音，VIDEO-视频，SYSTEM-系统消息
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息内容摘要（用于列表显示）
     */
    private String contentSummary;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件大小（字节）
     */
    private Long attachmentSize;

    /**
     * 是否已编辑：0-否，1-是
     */
    private Integer isEdited;

    /**
     * 编辑时间
     */
    private LocalDateTime editTime;

    /**
     * 是否已撤回：0-否，1-是
     */
    private Integer isRecalled;

    /**
     * 撤回时间
     */
    private LocalDateTime recallTime;

    /**
     * 已读人数
     */
    private Integer readCount;

    /**
     * 反应数量
     */
    private Integer reactionCount;

    /**
     * 父消息ID（用于回复）
     */
    private Long parentMessageId;

    /**
     * 引用消息ID
     */
    private Long referencedMessageId;

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