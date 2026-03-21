-- 校园平台用户服务数据库表结构
-- 数据库名称: campus_user

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_user`;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(50) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) UNIQUE,
  `phone` varchar(20) UNIQUE,
  `real_name` varchar(50),
  `student_id` varchar(50),
  `avatar_url` varchar(500),
  `gender` tinyint DEFAULT 0,
  `birthday` date,
  `school` varchar(100),
  `major` varchar(100),
  `grade` varchar(20),
  `address` varchar(500),
  `signature` varchar(200),
  `status` tinyint DEFAULT 1,
  `last_login_time` datetime,
  `last_login_ip` varchar(50),
  `is_deleted` tinyint DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int DEFAULT 0
);

-- 用户角色表
CREATE TABLE IF NOT EXISTS `user_roles` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_code` varchar(50) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (`user_id`, `role_code`)
);

-- 用户认证记录表
CREATE TABLE IF NOT EXISTS `user_auth_logs` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `auth_type` varchar(20) NOT NULL,
  `auth_status` tinyint NOT NULL,
  `ip_address` varchar(50),
  `user_agent` varchar(500),
  `authenticated_at` datetime DEFAULT CURRENT_TIMESTAMP
);

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorites` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_type` varchar(20) NOT NULL,
  `target_id` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (`user_id`, `target_type`, `target_id`)
);

-- 用户地址表（可选）
CREATE TABLE IF NOT EXISTS `user_addresses` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `receiver_name` varchar(50) NOT NULL,
  `receiver_phone` varchar(20) NOT NULL,
  `province` varchar(50),
  `city` varchar(50),
  `district` varchar(50),
  `detail_address` varchar(200) NOT NULL,
  `postal_code` varchar(10),
  `is_default` tinyint DEFAULT 0,
  `is_deleted` tinyint DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户钱包表（可选，用于积分、余额管理）
CREATE TABLE IF NOT EXISTS `user_wallets` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL UNIQUE,
  `balance` decimal(10,2) DEFAULT 0.00,
  `points` int DEFAULT 0,
  `frozen_balance` decimal(10,2) DEFAULT 0.00,
  `is_deleted` tinyint DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX `idx_username` ON `users`(`username`);
CREATE INDEX `idx_email` ON `users`(`email`);
CREATE INDEX `idx_phone` ON `users`(`phone`);
CREATE INDEX `idx_status` ON `users`(`status`);
CREATE INDEX `idx_created_at` ON `users`(`created_at`);
CREATE INDEX `idx_user_id` ON `user_roles`(`user_id`);
CREATE INDEX `idx_role_code` ON `user_roles`(`role_code`);
CREATE INDEX `idx_user_id_auth` ON `user_auth_logs`(`user_id`);
CREATE INDEX `idx_auth_type` ON `user_auth_logs`(`auth_type`);
CREATE INDEX `idx_authenticated_at` ON `user_auth_logs`(`authenticated_at`);
CREATE INDEX `idx_user_id_fav` ON `user_favorites`(`user_id`);
CREATE INDEX `idx_target` ON `user_favorites`(`target_type`, `target_id`);
CREATE INDEX `idx_user_id_addr` ON `user_addresses`(`user_id`);
CREATE INDEX `idx_is_default` ON `user_addresses`(`is_default`);
CREATE INDEX `idx_user_id_wallet` ON `user_wallets`(`user_id`);