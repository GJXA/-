-- 校园平台用户服务数据库表结构
-- 数据库名称: campus_user

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_user`;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) UNIQUE NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '加密密码',
  `email` varchar(100) UNIQUE COMMENT '邮箱',
  `phone` varchar(20) UNIQUE COMMENT '手机号',
  `real_name` varchar(50) COMMENT '真实姓名',
  `student_id` varchar(50) COMMENT '学号/工号',
  `avatar_url` varchar(500) COMMENT '头像URL',
  `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date COMMENT '生日',
  `school` varchar(100) COMMENT '学校',
  `major` varchar(100) COMMENT '专业',
  `grade` varchar(20) COMMENT '年级',
  `address` varchar(500) COMMENT '地址',
  `signature` varchar(200) COMMENT '个性签名',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-未激活',
  `last_login_time` datetime COMMENT '最后登录时间',
  `last_login_ip` varchar(50) COMMENT '最后登录IP',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT 0 COMMENT '版本号（乐观锁）',
  INDEX idx_username(`username`),
  INDEX idx_email(`email`),
  INDEX idx_phone(`phone`),
  INDEX idx_status(`status`),
  INDEX idx_create_time(`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户角色表
CREATE TABLE IF NOT EXISTS `user_roles` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码：USER-普通用户，SELLER-卖家，COMPANY-企业用户，ADMIN-管理员',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_user_role(`user_id`, `role_code`),
  INDEX idx_user_id(`user_id`),
  INDEX idx_role_code(`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色表';

-- 用户认证记录表
CREATE TABLE IF NOT EXISTS `user_auth_logs` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '认证记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `auth_type` varchar(20) NOT NULL COMMENT '认证类型：LOGIN-登录，REGISTER-注册，LOGOUT-登出，RESET_PASSWORD-重置密码',
  `auth_status` tinyint NOT NULL COMMENT '认证状态：0-失败，1-成功',
  `ip_address` varchar(50) COMMENT 'IP地址',
  `user_agent` varchar(500) COMMENT '用户代理',
  `auth_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '认证时间',
  INDEX idx_user_id(`user_id`),
  INDEX idx_auth_type(`auth_type`),
  INDEX idx_auth_time(`auth_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户认证记录表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorites` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) NOT NULL COMMENT '收藏类型：PRODUCT-商品，JOB-兼职',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_user_favorite(`user_id`, `target_type`, `target_id`),
  INDEX idx_user_id(`user_id`),
  INDEX idx_target(`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 用户地址表（可选）
CREATE TABLE IF NOT EXISTS `user_addresses` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(50) COMMENT '省份',
  `city` varchar(50) COMMENT '城市',
  `district` varchar(50) COMMENT '区县',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `postal_code` varchar(10) COMMENT '邮政编码',
  `is_default` tinyint DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id(`user_id`),
  INDEX idx_is_default(`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 用户钱包表（可选，用于积分、余额管理）
CREATE TABLE IF NOT EXISTS `user_wallets` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '钱包ID',
  `user_id` bigint NOT NULL UNIQUE COMMENT '用户ID',
  `balance` decimal(10,2) DEFAULT 0.00 COMMENT '余额',
  `points` int DEFAULT 0 COMMENT '积分',
  `frozen_balance` decimal(10,2) DEFAULT 0.00 COMMENT '冻结金额',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户钱包表';

-- 初始化管理员用户（可选）
-- 密码加密方式：BCrypt
-- INSERT INTO `users` (`username`, `password`, `email`, `real_name`, `status`)
-- VALUES ('admin', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'admin@campus.com', '系统管理员', 1);

-- 初始化管理员角色（可选）
-- INSERT INTO `user_roles` (`user_id`, `role_code`) VALUES (1, 'ADMIN');

-- 创建用户统计视图
CREATE OR REPLACE VIEW `user_statistics_view` AS
SELECT
    u.id as user_id,
    u.username,
    u.real_name,
    u.school,
    u.major,
    COUNT(DISTINCT ur.role_code) as role_count,
    MAX(ual.auth_time) as last_auth_time,
    COUNT(DISTINCT CASE WHEN uf.target_type = 'PRODUCT' THEN uf.id END) as product_favorites_count,
    COUNT(DISTINCT CASE WHEN uf.target_type = 'JOB' THEN uf.id END) as job_favorites_count
FROM `users` u
LEFT JOIN `user_roles` ur ON u.id = ur.user_id
LEFT JOIN `user_auth_logs` ual ON u.id = ual.user_id AND ual.auth_type = 'LOGIN' AND ual.auth_status = 1
LEFT JOIN `user_favorites` uf ON u.id = uf.user_id
WHERE u.deleted = 0
GROUP BY u.id, u.username, u.real_name, u.school, u.major;

-- 创建索引优化
CREATE INDEX `idx_users_composite` ON `users` (`status`, `create_time`);
CREATE INDEX `idx_auth_logs_composite` ON `user_auth_logs` (`user_id`, `auth_type`, `auth_time`);
CREATE INDEX `idx_favorites_composite` ON `user_favorites` (`user_id`, `target_type`, `create_time`);