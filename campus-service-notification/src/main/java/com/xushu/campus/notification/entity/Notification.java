package com.xushu.campus.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知信息实体类
 */
@Data
@TableName("notifications")
public class Notification {

    /**
     * 通知ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 通知类型：SYSTEM-系统通知，ORDER-订单通知，JOB-兼职通知，PRODUCT-商品通知，USER-用户通知
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知状态：0-未读，1-已读，2-已删除
     */
    private Integer status;

    /**
     * 通知优先级：0-低，1-普通，2-高，3-紧急
     */
    private Integer priority;

    /**
     * 发送方式：IN_APP-站内消息，EMAIL-电子邮件，SMS-短信，WEBSOCKET-WebSocket
     */
    private String channel;

    /**
     * 关联的业务ID（如订单ID、兼职ID、商品ID等）
     */
    private Long relatedId;

    /**
     * 关联的业务类型（冗余字段，方便查询）
     */
    private String relatedType;

    /**
     * 业务数据（JSON格式，存储额外的业务数据）
     */
    private String businessData;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 读取时间
     */
    private LocalDateTime readTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 邮件发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer emailStatus;

    /**
     * 邮件发送失败原因
     */
    private String emailFailureReason;

    /**
     * 邮件发送重试次数
     */
    private Integer emailRetryCount;

    /**
     * 短信发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer smsStatus;

    /**
     * 短信发送失败原因
     */
    private String smsFailureReason;

    /**
     * 短信发送重试次数
     */
    private Integer smsRetryCount;

    /**
     * WebSocket发送状态：0-未发送，1-发送成功，2-发送失败
     */
    private Integer websocketStatus;

    /**
     * 发送人ID（系统通知时为0）
     */
    private Long senderId;

    /**
     * 发送人名称
     */
    private String senderName;

    /**
     * 接收人邮箱（冗余存储，方便邮件发送）
     */
    private String receiverEmail;

    /**
     * 接收人手机号（冗余存储，方便短信发送）
     */
    private String receiverPhone;

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