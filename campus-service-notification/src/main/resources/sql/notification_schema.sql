-- 通知服务数据库脚本
-- 数据库名: campus_notification

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `campus_notification` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_notification`;

-- 通知信息表
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(50) NOT NULL COMMENT '通知类型：SYSTEM-系统通知，ORDER-订单通知，JOB-兼职通知，PRODUCT-商品通知，USER-用户通知',
  `title` varchar(200) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知内容',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '通知状态：0-未读，1-已读，2-已删除',
  `priority` tinyint NOT NULL DEFAULT '1' COMMENT '通知优先级：0-低，1-普通，2-高，3-紧急',
  `channel` varchar(50) NOT NULL DEFAULT 'IN_APP' COMMENT '发送方式：IN_APP-站内消息，EMAIL-电子邮件，SMS-短信，WEBSOCKET-WebSocket',
  `related_id` bigint DEFAULT NULL COMMENT '关联的业务ID（如订单ID、兼职ID、商品ID等）',
  `related_type` varchar(50) DEFAULT NULL COMMENT '关联的业务类型（冗余字段，方便查询）',
  `business_data` json DEFAULT NULL COMMENT '业务数据（JSON格式，存储额外的业务数据）',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `read_time` datetime DEFAULT NULL COMMENT '读取时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `email_status` tinyint DEFAULT '0' COMMENT '邮件发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败',
  `email_failure_reason` varchar(500) DEFAULT NULL COMMENT '邮件发送失败原因',
  `email_retry_count` int DEFAULT '0' COMMENT '邮件发送重试次数',
  `sms_status` tinyint DEFAULT '0' COMMENT '短信发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败',
  `sms_failure_reason` varchar(500) DEFAULT NULL COMMENT '短信发送失败原因',
  `sms_retry_count` int DEFAULT '0' COMMENT '短信发送重试次数',
  `websocket_status` tinyint DEFAULT '0' COMMENT 'WebSocket发送状态：0-未发送，1-发送成功，2-发送失败',
  `sender_id` bigint DEFAULT '0' COMMENT '发送人ID（系统通知时为0）',
  `sender_name` varchar(100) DEFAULT NULL COMMENT '发送人名称',
  `receiver_email` varchar(100) DEFAULT NULL COMMENT '接收人邮箱（冗余存储，方便邮件发送）',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '接收人手机号（冗余存储，方便短信发送）',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号（乐观锁）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_related_info` (`related_id`, `related_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知信息表';

-- 邮件模板表
CREATE TABLE IF NOT EXISTS `email_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(50) NOT NULL COMMENT '模板代码（唯一标识）',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `subject` varchar(200) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容（HTML格式）',
  `plain_text_content` text COMMENT '邮件内容（纯文本格式）',
  `variables` json DEFAULT NULL COMMENT '模板变量说明（JSON格式，描述模板中可用的变量）',
  `template_type` varchar(50) NOT NULL COMMENT '模板类型：VERIFICATION_CODE-验证码邮件，REGISTRATION_SUCCESS-注册成功邮件，PASSWORD_RESET-密码重置邮件，ORDER_CONFIRMATION-订单确认邮件，JOB_APPLICATION-兼职申请邮件，SYSTEM_NOTIFICATION-系统通知邮件，PROMOTIONAL-促销邮件',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号（乐观锁）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件模板表';

-- 初始化邮件模板数据
INSERT INTO `email_templates` (
  `template_code`, `template_name`, `subject`, `content`, `plain_text_content`, `template_type`, `enabled`, `remark`
) VALUES
(
  'USER_REGISTRATION_SUCCESS',
  '用户注册成功邮件',
  '欢迎加入校园二手交易与兼职平台',
  '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>欢迎加入校园二手交易与兼职平台</title>
</head>
<body>
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2>欢迎加入校园二手交易与兼职平台！</h2>
        <p>亲爱的${username}，</p>
        <p>感谢您注册我们的平台！您的账号已成功创建。</p>
        <p>现在您可以：</p>
        <ul>
            <li>浏览和发布二手商品</li>
            <li>查找和申请兼职工作</li>
            <li>与其他用户交流互动</li>
        </ul>
        <p>如果您有任何问题，请随时联系我们的客服团队。</p>
        <p>祝您使用愉快！</p>
        <hr>
        <p style="font-size: 12px; color: #666;">
            此邮件由系统自动发送，请勿回复。
        </p>
    </div>
</body>
</html>',
  '亲爱的${username}，感谢您注册我们的平台！您的账号已成功创建。现在您可以浏览和发布二手商品，查找和申请兼职工作，与其他用户交流互动。如果您有任何问题，请随时联系我们的客服团队。祝您使用愉快！',
  'REGISTRATION_SUCCESS',
  1,
  '用户注册成功欢迎邮件'
),
(
  'EMAIL_VERIFICATION_CODE',
  '邮箱验证码邮件',
  '您的邮箱验证码',
  '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>邮箱验证码</title>
</head>
<body>
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2>邮箱验证码</h2>
        <p>您好！</p>
        <p>您正在进行的操作需要验证您的邮箱地址，验证码为：</p>
        <div style="background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; margin: 20px 0;">
            ${verificationCode}
        </div>
        <p>验证码有效期：${expiryMinutes}分钟</p>
        <p>如果这不是您本人的操作，请忽略此邮件。</p>
        <hr>
        <p style="font-size: 12px; color: #666;">
            此邮件由系统自动发送，请勿回复。
        </p>
    </div>
</body>
</html>',
  '您好！您正在进行的操作需要验证您的邮箱地址，验证码为：${verificationCode}，验证码有效期：${expiryMinutes}分钟。如果这不是您本人的操作，请忽略此邮件。',
  'VERIFICATION_CODE',
  1,
  '邮箱验证码邮件模板'
),
(
  'ORDER_CONFIRMATION',
  '订单确认邮件',
  '您的订单已确认',
  '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>订单确认</title>
</head>
<body>
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2>订单确认</h2>
        <p>亲爱的${username}，</p>
        <p>您的订单已确认，订单信息如下：</p>
        <table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
            <tr>
                <td style="border: 1px solid #ddd; padding: 10px;"><strong>订单号</strong></td>
                <td style="border: 1px solid #ddd; padding: 10px;">${orderId}</td>
            </tr>
            <tr>
                <td style="border: 1px solid #ddd; padding: 10px;"><strong>商品名称</strong></td>
                <td style="border: 1px solid #ddd; padding: 10px;">${productName}</td>
            </tr>
            <tr>
                <td style="border: 1px solid #ddd; padding: 10px;"><strong>价格</strong></td>
                <td style="border: 1px solid #ddd; padding: 10px;">¥${price}</td>
            </tr>
            <tr>
                <td style="border: 1px solid #ddd; padding: 10px;"><strong>下单时间</strong></td>
                <td style="border: 1px solid #ddd; padding: 10px;">${orderTime}</td>
            </tr>
        </table>
        <p>您可以登录平台查看订单详情。</p>
        <hr>
        <p style="font-size: 12px; color: #666;">
            此邮件由系统自动发送，请勿回复。
        </p>
    </div>
</body>
</html>',
  '亲爱的${username}，您的订单已确认，订单号：${orderId}，商品：${productName}，价格：¥${price}，下单时间：${orderTime}。您可以登录平台查看订单详情。',
  'ORDER_CONFIRMATION',
  1,
  '订单确认邮件模板'
);

-- 创建索引
CREATE INDEX `idx_notifications_composite` ON `notifications` (`user_id`, `status`, `type`);
CREATE INDEX `idx_notifications_send_status` ON `notifications` (`send_time`, `status`);
CREATE INDEX `idx_email_templates_search` ON `email_templates` (`template_type`, `enabled`, `create_time`);

-- 创建视图：用户未读通知统计
CREATE VIEW `v_user_unread_notifications` AS
SELECT
    user_id,
    COUNT(*) as unread_count,
    MAX(send_time) as latest_send_time
FROM notifications
WHERE status = 0
    AND deleted = 0
    AND (expire_time IS NULL OR expire_time > NOW())
GROUP BY user_id;

-- 创建视图：通知类型统计
CREATE VIEW `v_notification_type_stats` AS
SELECT
    type,
    COUNT(*) as total_count,
    SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as unread_count,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as read_count
FROM notifications
WHERE deleted = 0
GROUP BY type;

-- 聊天会话表（支持用户间的即时通讯）
CREATE TABLE IF NOT EXISTS `conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `conversation_type` varchar(20) NOT NULL COMMENT '会话类型：PRIVATE-私聊，GROUP-群聊，SYSTEM-系统',
  `title` varchar(200) COMMENT '会话标题（群聊时使用）',
  `avatar_url` varchar(500) COMMENT '会话头像',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `last_message_id` bigint DEFAULT NULL COMMENT '最后一条消息ID',
  `last_message_content` varchar(500) DEFAULT NULL COMMENT '最后一条消息内容（冗余）',
  `last_message_time` datetime DEFAULT NULL COMMENT '最后一条消息时间',
  `unread_count` int DEFAULT 0 COMMENT '未读消息数',
  `member_count` int DEFAULT 0 COMMENT '成员数量',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号（乐观锁）',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_last_message_time` (`last_message_time`),
  KEY `idx_conversation_type` (`conversation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 会话成员表
CREATE TABLE IF NOT EXISTS `conversation_members` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成员关系ID',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_role` varchar(20) NOT NULL DEFAULT 'MEMBER' COMMENT '用户角色：OWNER-拥有者，ADMIN-管理员，MEMBER-成员',
  `nickname_in_conversation` varchar(100) COMMENT '用户在会话中的昵称',
  `is_muted` tinyint DEFAULT 0 COMMENT '是否静音：0-否，1-是',
  `last_read_message_id` bigint DEFAULT NULL COMMENT '最后读取的消息ID',
  `unread_count` int DEFAULT 0 COMMENT '未读消息数（针对该成员）',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `leave_time` datetime DEFAULT NULL COMMENT '离开时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conversation_user` (`conversation_id`, `user_id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话成员表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `message_type` varchar(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型：TEXT-文本，IMAGE-图片，FILE-文件，VOICE-语音，VIDEO-视频，SYSTEM-系统消息',
  `content` text NOT NULL COMMENT '消息内容',
  `content_summary` varchar(500) DEFAULT NULL COMMENT '消息内容摘要（用于列表显示）',
  `attachment_url` varchar(500) DEFAULT NULL COMMENT '附件URL',
  `attachment_name` varchar(200) DEFAULT NULL COMMENT '附件名称',
  `attachment_size` bigint DEFAULT NULL COMMENT '附件大小（字节）',
  `is_edited` tinyint DEFAULT 0 COMMENT '是否已编辑：0-否，1-是',
  `edit_time` datetime DEFAULT NULL COMMENT '编辑时间',
  `is_recalled` tinyint DEFAULT 0 COMMENT '是否已撤回：0-否，1-是',
  `recall_time` datetime DEFAULT NULL COMMENT '撤回时间',
  `read_count` int DEFAULT 0 COMMENT '已读人数',
  `reaction_count` int DEFAULT 0 COMMENT '反应数量',
  `parent_message_id` bigint DEFAULT NULL COMMENT '父消息ID（用于回复）',
  `referenced_message_id` bigint DEFAULT NULL COMMENT '引用消息ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号（乐观锁）',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_message_type` (`message_type`),
  KEY `idx_parent_message_id` (`parent_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 消息阅读状态表
CREATE TABLE IF NOT EXISTS `message_read_status` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阅读状态ID',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_user` (`message_id`, `user_id`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_read_time` (`read_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息阅读状态表';

-- 消息反应表（点赞、表情反应等）
CREATE TABLE IF NOT EXISTS `message_reactions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '反应ID',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `reaction_type` varchar(50) NOT NULL COMMENT '反应类型：LIKE-点赞，HEART-爱心，LAUGH-大笑，SAD-难过，ANGRY-生气',
  `reaction_emoji` varchar(10) DEFAULT NULL COMMENT '反应表情',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_user_reaction` (`message_id`, `user_id`, `reaction_type`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_reaction_type` (`reaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息反应表';

-- 创建聊天相关索引
CREATE INDEX `idx_conversations_composite` ON `conversations` (`conversation_type`, `last_message_time`);
CREATE INDEX `idx_messages_composite` ON `messages` (`conversation_id`, `create_time`);
CREATE INDEX `idx_messages_search` ON `messages` (`content`(100), `sender_id`);

-- 创建聊天视图：会话详情视图
CREATE OR REPLACE VIEW `conversation_detail_view` AS
SELECT
    c.*,
    cm.user_id as current_user_id,
    cm.unread_count as user_unread_count,
    cm.last_read_message_id,
    cm.is_muted,
    (SELECT COUNT(*) FROM messages m WHERE m.conversation_id = c.id AND m.deleted = 0) as total_message_count
FROM `conversations` c
LEFT JOIN `conversation_members` cm ON c.id = cm.conversation_id
WHERE c.deleted = 0 AND cm.deleted = 0;

-- 注释
-- 表名和字段名使用小写字母和下划线，符合MySQL命名规范
-- 所有表都包含逻辑删除字段(deleted)和乐观锁字段(version)
-- 使用utf8mb4字符集支持完整的Unicode字符（包括emoji）
-- 使用InnoDB引擎支持事务和外键