-- 校园平台商品服务数据库表结构
-- 数据库名称: campus_product

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_product` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_product`;

-- 商品表
CREATE TABLE IF NOT EXISTS `products` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '卖家ID',
  `title` varchar(200) NOT NULL COMMENT '商品标题',
  `description` text COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) COMMENT '原价',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-待审核，1-上架，2-下架，3-已售出',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `favorite_count` int DEFAULT 0 COMMENT '收藏数',
  `address` varchar(300) COMMENT '详细地址（包含位置信息）',
  `images` text COMMENT '商品图片URL列表（JSON格式）',
  `contact_phone` varchar(20) COMMENT '联系电话',
  `contact_name` varchar(50) COMMENT '联系人姓名',
  `quality_level` varchar(20) COMMENT '成色：NEW-全新，LIKE_NEW-几乎全新，USED-使用过',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `expire_time` datetime COMMENT '过期时间',
  `is_top` tinyint DEFAULT 0 COMMENT '是否置顶',
  `top_weight` int DEFAULT 0 COMMENT '置顶权重',
  `is_recommended` tinyint DEFAULT 0 COMMENT '是否推荐',
  `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT 0 COMMENT '版本号（乐观锁）',
  INDEX idx_user_id(`user_id`),
  INDEX idx_category_id(`category_id`),
  INDEX idx_status(`status`),
  INDEX idx_publish_time(`publish_time`),
  INDEX idx_is_top(`is_top`),
  INDEX idx_is_recommended(`is_recommended`),
  INDEX idx_price(`price`),
  INDEX idx_address(`address`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `product_categories` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `code` varchar(50) UNIQUE NOT NULL COMMENT '分类编码',
  `description` varchar(200) COMMENT '描述',
  `icon` varchar(200) COMMENT '图标',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint DEFAULT 1 COMMENT '是否启用',
  `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_parent_id(`parent_id`),
  INDEX idx_code(`code`),
  INDEX idx_sort_order(`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品图片表
CREATE TABLE IF NOT EXISTS `product_images` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `thumbnail_url` varchar(500) COMMENT '缩略图URL',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `is_main` tinyint DEFAULT 0 COMMENT '是否主图：0-否，1-是',
  `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_product_id(`product_id`),
  INDEX idx_sort_order(`sort_order`),
  INDEX idx_is_main(`is_main`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- 商品浏览记录表
CREATE TABLE IF NOT EXISTS `product_view_logs` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '浏览记录ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint COMMENT '用户ID（可为空）',
  `ip_address` varchar(50) COMMENT 'IP地址',
  `user_agent` varchar(500) COMMENT '用户代理',
  `view_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  INDEX idx_product_id(`product_id`),
  INDEX idx_user_id(`user_id`),
  INDEX idx_view_time(`view_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品浏览记录表';

-- 商品点赞表
CREATE TABLE IF NOT EXISTS `product_likes` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_product_user(`product_id`, `user_id`),
  INDEX idx_product_id(`product_id`),
  INDEX idx_user_id(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品点赞表';

-- 商品评论表
CREATE TABLE IF NOT EXISTS `product_comments` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_id` bigint DEFAULT 0 COMMENT '父评论ID',
  `content` text NOT NULL COMMENT '评论内容',
  `rating` tinyint COMMENT '评分（1-5）',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-隐藏，1-显示，2-审核中',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_product_id(`product_id`),
  INDEX idx_user_id(`user_id`),
  INDEX idx_parent_id(`parent_id`),
  INDEX idx_status(`status`),
  INDEX idx_created_at(`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评论表';

-- 商品举报表
CREATE TABLE IF NOT EXISTS `product_reports` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '举报ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `reporter_id` bigint NOT NULL COMMENT '举报人ID',
  `report_type` varchar(50) NOT NULL COMMENT '举报类型：FAKE-虚假信息，ILLEGAL-违法信息，OTHER-其他',
  `report_reason` varchar(500) NOT NULL COMMENT '举报原因',
  `evidence_images` varchar(1000) COMMENT '证据图片（JSON数组）',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已处理，3-已驳回',
  `processor_id` bigint COMMENT '处理人ID',
  `process_time` datetime COMMENT '处理时间',
  `process_result` varchar(500) COMMENT '处理结果',
  `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_product_id(`product_id`),
  INDEX idx_reporter_id(`reporter_id`),
  INDEX idx_status(`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品举报表';

-- 商品统计数据表（用于缓存统计结果）
CREATE TABLE IF NOT EXISTS `product_statistics` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
  `product_id` bigint COMMENT '商品ID',
  `category_id` bigint COMMENT '分类ID',
  `total_views` int DEFAULT 0 COMMENT '总浏览量',
  `total_likes` int DEFAULT 0 COMMENT '总点赞数',
  `total_favorites` int DEFAULT 0 COMMENT '总收藏数',
  `total_comments` int DEFAULT 0 COMMENT '总评论数',
  `total_sales` int DEFAULT 0 COMMENT '总销量',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_statistics(`product_id`, `category_id`, `statistics_date`),
  INDEX idx_category_id(`category_id`),
  INDEX idx_statistics_date(`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品统计数据表';

-- 初始化商品分类数据
INSERT IGNORE INTO `product_categories` (`parent_id`, `name`, `code`, `description`, `sort_order`) VALUES
(0, '电子产品', 'ELECTRONICS', '手机、电脑、平板等', 1),
(0, '学习用品', 'STUDY_SUPPLIES', '书籍、文具、教材等', 2),
(0, '生活用品', 'DAILY_NECESSITIES', '日用品、化妆品等', 3),
(0, '服饰鞋包', 'CLOTHING_SHOES', '衣服、鞋子、包包等', 4),
(0, '体育用品', 'SPORTS_GOODS', '运动器材、健身用品等', 5),
(0, '其他', 'OTHER', '其他物品', 6);

-- 电子产品子分类
INSERT IGNORE INTO `product_categories` (`parent_id`, `name`, `code`, `description`, `sort_order`) VALUES
(1, '手机', 'PHONE', '智能手机、功能机', 1),
(1, '电脑', 'COMPUTER', '笔记本电脑、台式机', 2),
(1, '平板', 'TABLET', '平板电脑', 3),
(1, '数码配件', 'DIGITAL_ACCESSORIES', '耳机、充电器、数据线等', 4);

-- 学习用品子分类
INSERT IGNORE INTO `product_categories` (`parent_id`, `name`, `code`, `description`, `sort_order`) VALUES
(2, '教材', 'TEXTBOOK', '各专业教材', 1),
(2, '参考书', 'REFERENCE_BOOK', '考研、考证参考书', 2),
(2, '文具', 'STATIONERY', '笔、本子、文具盒等', 3),
(2, '学习工具', 'STUDY_TOOLS', '计算器、尺子等', 4);

-- 创建商品详情视图
CREATE OR REPLACE VIEW `product_detail_view` AS
SELECT
    p.*,
    pc.name as category_name,
    pc.code as category_code,
    (SELECT COUNT(*) FROM product_likes pl WHERE pl.product_id = p.id) as actual_like_count,
    (SELECT COUNT(*) FROM product_comments pc WHERE pc.product_id = p.id AND pc.status = 1 AND pc.is_deleted = 0) as actual_comment_count,
    (SELECT image_url FROM product_images pi WHERE pi.product_id = p.id AND pi.is_main = 1 AND pi.is_deleted = 0 LIMIT 1) as main_image
FROM `products` p
LEFT JOIN `product_categories` pc ON p.category_id = pc.id
WHERE p.is_deleted = 0;

-- 创建热门商品视图
CREATE OR REPLACE VIEW `hot_products_view` AS
SELECT
    p.id,
    p.title,
    p.price,
    p.view_count,
    p.like_count,
    p.favorite_count,
    pc.name as category_name,
    (SELECT image_url FROM product_images pi WHERE pi.product_id = p.id AND pi.is_main = 1 AND pi.is_deleted = 0 LIMIT 1) as main_image,
    ROW_NUMBER() OVER (PARTITION BY p.category_id ORDER BY (p.view_count * 0.3 + p.like_count * 0.4 + p.favorite_count * 0.3) DESC) as hot_rank
FROM `products` p
LEFT JOIN `product_categories` pc ON p.category_id = pc.id
WHERE p.status = 1
  AND p.is_deleted = 0
  AND p.expire_time > NOW();

-- 创建索引优化
CREATE INDEX `idx_products_composite` ON `products` (`status`, `category_id`, `publish_time`);
CREATE INDEX `idx_products_search` ON `products` (`title`, `description`(100), `address`(100));
CREATE INDEX `idx_categories_tree` ON `product_categories` (`parent_id`, `sort_order`);
CREATE INDEX `idx_view_logs_composite` ON `product_view_logs` (`product_id`, `view_time`);

-- 创建存储过程：更新商品统计数据
DELIMITER //
DROP PROCEDURE IF EXISTS `sp_update_product_statistics`//
CREATE PROCEDURE `sp_update_product_statistics`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_product_id BIGINT;
    DECLARE v_category_id BIGINT;
    DECLARE cur CURSOR FOR
        SELECT id, category_id FROM products WHERE is_deleted = 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_product_id, v_category_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        INSERT INTO product_statistics
            (product_id, category_id, total_views, total_likes, total_favorites, total_comments, statistics_date, created_at)
        SELECT
            v_product_id,
            v_category_id,
            COALESCE(SUM(pvl.view_count), 0) as total_views,
            COALESCE(COUNT(DISTINCT pl.id), 0) as total_likes,
            COALESCE(COUNT(DISTINCT uf.id), 0) as total_favorites,
            COALESCE(COUNT(DISTINCT pc.id), 0) as total_comments,
            CURDATE(),
            NOW()
        FROM products p
        LEFT JOIN product_view_logs pvl ON p.id = pvl.product_id
        LEFT JOIN product_likes pl ON p.id = pl.product_id
        LEFT JOIN user_favorites uf ON uf.target_type = 'PRODUCT' AND uf.target_id = p.id
        LEFT JOIN product_comments pc ON p.id = pc.product_id AND pc.status = 1 AND pc.is_deleted = 0
        WHERE p.id = v_product_id
        ON DUPLICATE KEY UPDATE
            total_views = VALUES(total_views),
            total_likes = VALUES(total_likes),
            total_favorites = VALUES(total_favorites),
            total_comments = VALUES(total_comments),
            created_at = NOW();
    END LOOP;

    CLOSE cur;
END//
DELIMITER ;