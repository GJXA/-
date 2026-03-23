-- 校园平台商品服务数据库表结构
-- 数据库名称: campus_product

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_product` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_product`;

-- 禁用外键约束
SET FOREIGN_KEY_CHECKS=0;

-- 删除表（按依赖顺序，从叶子到根）
DROP TABLE IF EXISTS `product_statistics`;
DROP TABLE IF EXISTS `product_reports`;
DROP TABLE IF EXISTS `product_comments`;
DROP TABLE IF EXISTS `product_likes`;
DROP TABLE IF EXISTS `product_view_logs`;
DROP TABLE IF EXISTS `product_images`;
DROP TABLE IF EXISTS `product_categories`;
DROP TABLE IF EXISTS `products`;

-- 启用外键约束
SET FOREIGN_KEY_CHECKS=1;

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

-- 初始化商品数据（测试数据）
INSERT IGNORE INTO `products` (`id`, `user_id`, `title`, `description`, `price`, `original_price`, `category_id`, `status`, `view_count`, `like_count`, `favorite_count`, `address`, `contact_phone`, `contact_name`, `quality_level`, `publish_time`, `expire_time`, `is_top`, `is_recommended`) VALUES
(1, 1, 'iPhone 14 Pro 256G 暗紫色 95新', '自用iPhone 14 Pro，256G暗紫色，使用一年半，电池健康度89%，无磕碰无划痕，屏幕完美。原装配件齐全，送透明保护壳。', 4500.00, 8999.00, 1, 1, 328, 45, 23, '北京大学校内面交', '13800001001', '张三', 'LIKE_NEW', '2026-03-01 10:00:00', '2026-06-01 10:00:00', 1, 1),
(2, 2, 'MacBook Air M2 8+256G 午夜色', '22年款MacBook Air M2，8G+256G午夜色，充电循环次数78次，成色98新，适合日常学习和轻度开发。', 5800.00, 9499.00, 1, 1, 256, 38, 19, '清华大学校内面交', '13800001002', '李四', 'LIKE_NEW', '2026-03-02 14:00:00', '2026-06-02 14:00:00', 1, 1),
(3, 6, 'iPad Air 5 64G WiFi 星光色', '毕业转让iPad Air 5，64G WiFi版本，平时上课记笔记用，配有Apple Pencil 2代和妙控键盘。', 2800.00, 4799.00, 3, 1, 189, 28, 15, '北京大学快递或面交均可', '13800001006', '周八', 'USED', '2026-03-05 09:00:00', '2026-06-05 09:00:00', 0, 1),
(4, 7, 'AirPods Pro 2 USB-C版 全新未拆', '双十一多买了一副，全新未拆封，支持验证。', 1350.00, 1899.00, 4, 1, 412, 62, 31, '上海交通大学闵行校区面交', '13800001007', '吴九', 'NEW', '2026-03-03 16:00:00', '2026-06-03 16:00:00', 0, 1),
(5, 1, '戴尔U2723QE 4K显示器 27寸', '戴尔U2723QE 27寸4K IPS显示器，Type-C 90W供电，完美屏无亮点暗点，使用8个月，箱说齐全。', 2200.00, 3999.00, 2, 1, 167, 22, 11, '北京大学宿舍自提', '13800001001', '张三', 'LIKE_NEW', '2026-03-06 11:00:00', '2026-06-06 11:00:00', 0, 0),
(6, 3, '考研408全套教材+真题 2026版', '计算机考研408全套：数据结构（严蔚敏）、操作系统（汤小丹）、计算机网络（谢希仁）、计组（唐朔飞），配套王道考研真题集，有少量笔记标注。', 120.00, 380.00, 5, 1, 534, 78, 52, '复旦大学邯郸校区', '13800001003', '王五', 'USED', '2026-03-01 08:00:00', '2026-05-01 08:00:00', 1, 1),
(7, 4, '雅思全套备考资料 剑桥4-18', '剑桥雅思真题4-18全套，加顾家北手把手、刘洪波阅读真经、王陆语料库。部分有标注，不影响使用。', 150.00, 500.00, 6, 1, 312, 41, 28, '浙江大学紫金港校区', '13800001004', '赵六', 'USED', '2026-03-04 10:30:00', '2026-05-04 10:30:00', 0, 1),
(8, 6, '卡西欧fx-991CN X 科学计算器', '大学物理和高数必备，功能完好，九成新，送电池。', 45.00, 139.00, 8, 1, 89, 12, 5, '北京大学校内', '13800001006', '周八', 'USED', '2026-03-07 15:00:00', '2026-06-07 15:00:00', 0, 0),
(9, 5, '戴森V10 Fluffy吸尘器', '宿舍用了一学期，吸力强劲，含地板刷头和缝隙吸头，滤芯已清洗。', 1200.00, 2990.00, 3, 1, 145, 19, 8, '南京大学仙林校区', '13800001005', '孙七', 'USED', '2026-03-08 12:00:00', '2026-06-08 12:00:00', 0, 0),
(10, 8, '九阳破壁豆浆机 全新', '学校活动奖品，全新未使用，有发票。', 180.00, 399.00, 3, 1, 98, 14, 7, '武汉大学校内面交', '13800001008', '郑十', 'NEW', '2026-03-09 10:00:00', '2026-06-09 10:00:00', 0, 0),
(11, 4, 'Nike Air Force 1 白色 39码 全新', '双十一冲动消费买的，码数偏大，全新带吊牌未穿过。', 450.00, 799.00, 4, 1, 223, 35, 18, '浙江大学紫金港快递', '13800001004', '赵六', 'NEW', '2026-03-10 14:30:00', '2026-06-10 14:30:00', 0, 1),
(12, 8, 'JanSport双肩包 海军蓝', '用了两个月的JanSport书包，容量大，适合上课装电脑。', 80.00, 249.00, 4, 1, 67, 8, 3, '武汉大学校内', '13800001008', '郑十', 'LIKE_NEW', '2026-03-11 09:00:00', '2026-06-11 09:00:00', 0, 0),
(13, 7, '迪卡侬公路自行车 RC120', '骑了半年的公路车，变速顺畅，刹车灵敏，适合校园通勤。', 1500.00, 2799.00, 5, 1, 198, 26, 14, '上海交通大学闵行校区', '13800001007', '吴九', 'USED', '2026-03-12 16:00:00', '2026-06-12 16:00:00', 0, 1),
(14, 3, '李宁羽毛球拍 风暴WS72', '入门级进攻拍，手感轻盈，送3个羽毛球和拍包。', 120.00, 299.00, 5, 1, 76, 10, 4, '复旦大学邯郸校区', '13800001003', '王五', 'USED', '2026-03-13 11:00:00', '2026-06-13 11:00:00', 0, 0),
(15, 2, '索尼WH-1000XM5 降噪耳机 黑色', '图书馆学习神器，佩戴舒适，降噪效果一流。使用半年，成色95新。', 1600.00, 2999.00, 6, 1, 287, 43, 22, '清华大学校内面交', '13800001002', '李四', 'LIKE_NEW', '2026-03-14 13:00:00', '2026-06-14 13:00:00', 0, 1);

-- 初始化商品点赞数据
INSERT IGNORE INTO `product_likes` (`product_id`, `user_id`) VALUES
(1, 2), (1, 3), (1, 4), (1, 5), (1, 7),
(2, 1), (2, 3), (2, 5), (2, 6),
(3, 1), (3, 2), (3, 4),
(4, 1), (4, 2), (4, 3), (4, 5), (4, 6), (4, 8),
(6, 1), (6, 2), (6, 4), (6, 5), (6, 7), (6, 8),
(7, 1), (7, 3), (7, 5),
(11, 1), (11, 3), (11, 5), (11, 7),
(13, 1), (13, 3), (13, 4),
(15, 1), (15, 3), (15, 4), (15, 5);

-- 初始化商品评论数据
INSERT IGNORE INTO `product_comments` (`id`, `product_id`, `user_id`, `content`, `rating`, `status`) VALUES
(1, 1, 2, '成色确实不错，价格合理', 5, 1),
(2, 1, 3, '请问电池健康度能截个图吗？', NULL, 1),
(3, 1, 1, '可以的，私信发你', NULL, 1),
(4, 2, 5, 'M2芯片的Air性能够用吗？想做轻度开发', NULL, 1),
(5, 2, 2, '日常开发完全没问题，跑Docker稍微吃力', NULL, 1),
(6, 4, 3, '全新未拆？可以当面验货吗？', NULL, 1),
(7, 4, 7, '当然可以，闵行校区面交，当面拆封验证', NULL, 1),
(8, 6, 5, '这套资料太全了！还有笔记标注，性价比超高', 5, 1),
(9, 6, 7, '请问数据结构那本有没有课后题答案？', NULL, 1),
(10, 13, 1, '这车骑起来怎么样？上坡费力吗？', NULL, 1),
(11, 13, 7, '校园里骑完全没问题，上坡稍微费点力，毕竟是公路车', NULL, 1),
(12, 15, 6, '降噪效果真的好吗？图书馆够用吗？', NULL, 1),
(13, 15, 2, '降噪效果一流，开了降噪基本听不到外界声音', NULL, 1);

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

