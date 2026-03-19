-- 校园平台订单服务数据库表结构
-- 数据库名称: campus_order

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_order`;

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单号',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID（买家）',
    `product_id` bigint(20) NOT NULL COMMENT '商品ID',
    `product_title` varchar(255) NOT NULL COMMENT '商品标题',
    `product_price` decimal(10,2) NOT NULL COMMENT '商品价格',
    `product_image` varchar(500) COMMENT '商品图片',
    `quantity` int(11) NOT NULL DEFAULT 1 COMMENT '商品数量',
    `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
    `payment_method` varchar(20) NOT NULL COMMENT '支付方式：ALIPAY-支付宝，WECHAT-微信，CASH-现金',
    `payment_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款',
    `order_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-已关闭',
    `receiver_name` varchar(100) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
    `receiver_address` varchar(500) NOT NULL COMMENT '收货地址',
    `buyer_message` varchar(500) COMMENT '买家留言',
    `seller_message` varchar(500) COMMENT '卖家留言',
    `payment_time` datetime COMMENT '支付时间',
    `delivery_time` datetime COMMENT '发货时间',
    `confirm_time` datetime COMMENT '确认收货时间',
    `cancel_time` datetime COMMENT '取消时间',
    `cancel_reason` varchar(500) COMMENT '取消原因',
    `expire_time` datetime NOT NULL COMMENT '订单过期时间',
    `seller_id` bigint(20) NOT NULL COMMENT '卖家ID',
    `seller_nickname` varchar(100) COMMENT '卖家昵称',
    `buyer_nickname` varchar(100) COMMENT '买家昵称',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version` int(11) NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_payment_status` (`payment_status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单操作记录表（记录订单状态变更历史）
CREATE TABLE IF NOT EXISTS `order_operation_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `order_id` bigint(20) NOT NULL COMMENT '订单ID',
    `operation_type` varchar(50) NOT NULL COMMENT '操作类型：CREATE-创建，CANCEL-取消，PAY-支付，DELIVER-发货，CONFIRM_RECEIPT-确认收货，APPLY_REFUND-申请退款，PROCESS_REFUND-处理退款',
    `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
    `operator_type` varchar(20) NOT NULL COMMENT '操作人类型：BUYER-买家，SELLER-卖家，ADMIN-管理员，SYSTEM-系统',
    `old_status` varchar(100) COMMENT '旧状态（JSON格式）',
    `new_status` varchar(100) COMMENT '新状态（JSON格式）',
    `operation_remark` varchar(500) COMMENT '操作备注',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单操作记录表';

-- 退款记录表
CREATE TABLE IF NOT EXISTS `order_refund` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款记录ID',
    `order_id` bigint(20) NOT NULL COMMENT '订单ID',
    `refund_no` varchar(64) NOT NULL COMMENT '退款单号',
    `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
    `refund_reason` varchar(500) NOT NULL COMMENT '退款原因',
    `refund_description` varchar(1000) COMMENT '退款说明',
    `refund_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '退款状态：0-待处理，1-已批准，2-已拒绝，3-已完成',
    `applicant_id` bigint(20) NOT NULL COMMENT '申请人ID',
    `applicant_type` varchar(20) NOT NULL COMMENT '申请人类型：BUYER-买家，SELLER-卖家',
    `approver_id` bigint(20) COMMENT '审批人ID',
    `approve_time` datetime COMMENT '审批时间',
    `approve_remark` varchar(500) COMMENT '审批备注',
    `refund_time` datetime COMMENT '退款时间',
    `refund_voucher` varchar(200) COMMENT '退款凭证',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_refund_status` (`refund_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款记录表';

-- 发货信息表
CREATE TABLE IF NOT EXISTS `order_delivery` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '发货记录ID',
    `order_id` bigint(20) NOT NULL COMMENT '订单ID',
    `express_company` varchar(100) NOT NULL COMMENT '快递公司',
    `express_number` varchar(100) NOT NULL COMMENT '快递单号',
    `delivery_note` varchar(500) COMMENT '发货备注',
    `delivery_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '发货状态：0-已发货，1-运输中，2-已签收',
    `estimated_arrival` datetime COMMENT '预计到达时间',
    `actual_arrival` datetime COMMENT '实际到达时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_express_number` (`express_number`),
    KEY `idx_delivery_status` (`delivery_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发货信息表';

-- 初始化数据（可选）
-- INSERT INTO `orders` (...) VALUES (...);

-- 创建索引的补充语句
-- ALTER TABLE `orders` ADD INDEX `idx_composite_status` (`order_status`, `payment_status`);

-- 创建视图：订单详情视图
CREATE OR REPLACE VIEW `order_detail_view` AS
SELECT
    o.*,
    od.express_company,
    od.express_number,
    od.delivery_status,
    od.estimated_arrival,
    od.actual_arrival,
    orf.refund_amount,
    orf.refund_status,
    orf.refund_reason
FROM `orders` o
LEFT JOIN `order_delivery` od ON o.id = od.order_id AND od.delivery_status != 3
LEFT JOIN `order_refund` orf ON o.id = orf.order_id AND orf.refund_status IN (0, 1, 3)
WHERE o.deleted = 0;

-- 创建存储过程：取消超时订单
DELIMITER //
CREATE PROCEDURE `sp_cancel_expired_orders`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_order_id BIGINT;
    DECLARE v_order_no VARCHAR(64);
    DECLARE cur CURSOR FOR
        SELECT id, order_no FROM orders
        WHERE order_status = 0
          AND payment_status = 0
          AND deleted = 0
          AND expire_time < NOW();
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_order_id, v_order_no;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 更新订单状态
        UPDATE orders
        SET order_status = 4,
            cancel_time = NOW(),
            cancel_reason = '超时未支付，自动取消',
            update_time = NOW()
        WHERE id = v_order_id;

        -- 记录操作日志
        INSERT INTO order_operation_log
            (order_id, operation_type, operator_id, operator_type, operation_remark, create_time)
        VALUES
            (v_order_id, 'CANCEL', 0, 'SYSTEM', '系统自动取消超时未支付订单', NOW());
    END LOOP;

    CLOSE cur;
END//
DELIMITER ;

-- 创建定时事件：每天凌晨执行取消超时订单
-- 注意：需要确保事件调度器已启用（SET GLOBAL event_scheduler = ON;）
CREATE EVENT IF NOT EXISTS `event_cancel_expired_orders`
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY)
DO
    CALL sp_cancel_expired_orders();

-- 注释：在生产环境中，建议使用应用程序的定时任务而不是数据库事件