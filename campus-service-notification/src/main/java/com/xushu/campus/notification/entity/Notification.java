package com.xushu.campus.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xushu.campus.notification.constant.NotificationConstants;
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
     * 发送人ID（系统通知时为0）
     */
    private Long senderId;


    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;

    /**
     * 邮件发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer emailStatus;

    /**
     * 短信发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer smsStatus;

    /**
     * WebSocket发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败
     */
    private Integer websocketStatus;

    /**
     * 发送人姓名（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String senderName;

    /**
     * 是否需要发送邮件
     */
    public boolean needSendEmail() {
        return this.channel != null && this.channel.contains(NotificationConstants.NotificationChannel.EMAIL);
    }

    /**
     * 是否需要发送短信
     */
    public boolean needSendSms() {
        return this.channel != null && this.channel.contains(NotificationConstants.NotificationChannel.SMS);
    }

    /**
     * 是否需要发送WebSocket消息
     */
    public boolean needSendWebSocket() {
        return this.channel != null && this.channel.contains(NotificationConstants.NotificationChannel.WEBSOCKET);
    }
}