-- 校园平台兼职服务数据库表结构
-- 数据库名: campus_job

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `campus_job` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 1. 兼职信息表
CREATE TABLE IF NOT EXISTS `jobs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '兼职ID',
  `title` varchar(200) NOT NULL COMMENT '兼职标题',
  `description` text COMMENT '兼职描述',
  `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资（单位：元/小时 或 元/天）',
  `salary_unit` varchar(20) DEFAULT NULL COMMENT '薪资单位：HOUR-时薪，DAY-日薪，MONTH-月薪，PROJECT-项目制',
  `job_type` varchar(20) DEFAULT NULL COMMENT '工作类型：FULL_TIME-全职，PART_TIME-兼职，INTERNSHIP-实习',
  `category` varchar(20) DEFAULT NULL COMMENT '工作类别：SALES-销售，TUTOR-家教，WAITER-服务员，PROMOTION-促销，OTHER-其他',
  `location` varchar(100) DEFAULT NULL COMMENT '工作地点',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `start_date` date DEFAULT NULL COMMENT '工作开始日期',
  `end_date` date DEFAULT NULL COMMENT '工作结束日期',
  `work_time` varchar(100) DEFAULT NULL COMMENT '工作时间（描述，如：周一至周五 9:00-18:00）',
  `recruit_count` int DEFAULT '1' COMMENT '招聘人数',
  `applied_count` int DEFAULT '0' COMMENT '已申请人数',
  `accepted_count` int DEFAULT '0' COMMENT '已录取人数',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系人电话',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系人邮箱',
  `company_name` varchar(100) DEFAULT NULL COMMENT '企业/机构名称',
  `company_description` text COMMENT '企业描述',
  `company_logo` varchar(500) DEFAULT NULL COMMENT '企业Logo',
  `requirements` text COMMENT '职位要求',
  `benefits` text COMMENT '福利待遇',
  `status` int DEFAULT '0' COMMENT '工作状态：0-待审核，1-招聘中，2-已结束，3-已关闭',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `favorite_count` int DEFAULT '0' COMMENT '收藏次数',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `top_weight` int DEFAULT '0' COMMENT '置顶权重（数值越大越靠前）',
  `is_recommended` tinyint(1) DEFAULT '0' COMMENT '是否推荐',
  `publisher_id` bigint NOT NULL COMMENT '发布者ID（企业用户或管理员）',
  `publisher_type` varchar(20) DEFAULT NULL COMMENT '发布者类型：USER-个人用户，COMPANY-企业用户，ADMIN-管理员',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '版本号（乐观锁）',
  PRIMARY KEY (`id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_status` (`status`),
  KEY `idx_job_type` (`job_type`),
  KEY `idx_category` (`category`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_deadline` (`deadline`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_is_recommended` (`is_recommended`),
  KEY `idx_is_deleted` (`is_deleted`)
  -- 注意：这里没有添加外键约束，因为job_categories表在后面定义
  -- 如果需要外键约束，可以在创建job_categories表后使用ALTER TABLE添加
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职信息表';

-- 2. 兼职申请表
CREATE TABLE IF NOT EXISTS `job_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `job_id` bigint NOT NULL COMMENT '兼职ID',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID',
  `applicant_grade` varchar(50) DEFAULT NULL COMMENT '申请人年级/专业',
  `resume` text COMMENT '简历内容',
  `apply_remark` varchar(500) DEFAULT NULL COMMENT '申请备注',
  `status` int DEFAULT '0' COMMENT '申请状态：0-待处理，1-已通过，2-已拒绝，3-已取消',
  `confirm_status` int DEFAULT '0' COMMENT '确认状态：0-未确认，1-确认参加，2-拒绝参加',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `processor_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `process_remark` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `interview_time` datetime DEFAULT NULL COMMENT '面试时间',
  `interview_location` varchar(200) DEFAULT NULL COMMENT '面试地点',
  `interview_remark` varchar(500) DEFAULT NULL COMMENT '面试备注',
  `offer_time` datetime DEFAULT NULL COMMENT '录用通知时间',
  `offer_content` text COMMENT '录用通知内容',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `confirm_remark` varchar(500) DEFAULT NULL COMMENT '确认备注',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始工作时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束工作时间',
  `work_evaluation` text COMMENT '工作评价',
  `evaluation_score` int DEFAULT NULL COMMENT '评价分数（1-5分）',
  `evaluation_time` datetime DEFAULT NULL COMMENT '评价时间',
  -- 系统字段
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_applicant_job` (`applicant_id`, `job_id`, `is_deleted`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_applicant_id` (`applicant_id`),
  KEY `idx_status` (`status`),
  KEY `idx_confirm_status` (`confirm_status`),
  KEY `idx_apply_time` (`apply_time`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_application_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职申请表';

-- 3. 兼职统计数据表（可选，用于缓存统计结果）
CREATE TABLE IF NOT EXISTS `job_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `publisher_id` bigint DEFAULT NULL COMMENT '发布者ID',
  `job_id` bigint DEFAULT NULL COMMENT '兼职ID',
  `total_jobs` int DEFAULT '0' COMMENT '总兼职数',
  `active_jobs` int DEFAULT '0' COMMENT '活跃兼职数',
  `total_applications` int DEFAULT '0' COMMENT '总申请数',
  `pending_applications` int DEFAULT '0' COMMENT '待处理申请数',
  `approved_applications` int DEFAULT '0' COMMENT '已通过申请数',
  `rejected_applications` int DEFAULT '0' COMMENT '已拒绝申请数',
  `total_views` int DEFAULT '0' COMMENT '总浏览量',
  `total_favorites` int DEFAULT '0' COMMENT '总收藏量',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_statistics` (`publisher_id`, `job_id`, `statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职统计数据表';

-- 4. 兼职分类表（用于管理分类，可选）
CREATE TABLE IF NOT EXISTS `job_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `code` varchar(50) NOT NULL COMMENT '分类编码（唯一标识）',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` varchar(500) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(200) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_is_enabled` (`is_enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职分类表';

-- 初始化兼职分类数据
INSERT IGNORE INTO `job_categories` (`code`, `name`, `description`, `sort_order`) VALUES
('SALES', '销售', '销售类兼职工作', 1),
('TUTOR', '家教', '家教辅导类工作', 2),
('WAITER', '服务员', '餐饮服务类工作', 3),
('PROMOTION', '促销', '促销推广类工作', 4),
('CLERK', '文员', '文员办公类工作', 5),
('DESIGN', '设计', '设计类工作', 6),
('PROGRAMMING', '编程/IT', '编程和IT类工作', 7),
('TRANSLATION', '翻译', '翻译类工作', 8),
('OTHER', '其他', '其他类型工作', 9);

-- 5. 兼职收藏表（虽然用户服务已有user_favorites表，但这里提供专门表作为冗余或备用）
CREATE TABLE IF NOT EXISTS `job_favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `job_id` bigint NOT NULL COMMENT '兼职ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_job_user` (`job_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_favorite_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职收藏表';

-- 6. 兼职浏览记录表
CREATE TABLE IF NOT EXISTS `job_view_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '浏览记录ID',
  `job_id` bigint NOT NULL COMMENT '兼职ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID（可为空）',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `viewed_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_viewed_at` (`viewed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职浏览记录表';

-- 7. 创建索引优化
CREATE INDEX `idx_jobs_composite` ON `jobs` (`status`, `job_type`, `category`, `publish_time`);
CREATE INDEX `idx_jobs_search` ON `jobs` (`title`, `description`(100), `location`);
CREATE INDEX `idx_applications_composite` ON `job_applications` (`job_id`, `status`, `apply_time`);
CREATE INDEX `idx_applications_user` ON `job_applications` (`applicant_id`, `status`, `apply_time`);

-- 8. 创建视图：兼职详情视图
CREATE OR REPLACE VIEW `job_detail_view` AS
SELECT
    j.*,
    jc.name as category_name,
    jc.description as category_description,
    (SELECT COUNT(*) FROM job_applications ja WHERE ja.job_id = j.id AND ja.status = 1) as actual_accepted_count,
    (SELECT COUNT(*) FROM job_favorites jf WHERE jf.job_id = j.id) as actual_favorite_count,
    (SELECT COUNT(*) FROM job_view_logs jvl WHERE jvl.job_id = j.id) as actual_view_count
FROM `jobs` j
LEFT JOIN `job_categories` jc ON j.category = jc.code
WHERE j.is_deleted = 0;

-- 9. 创建热门兼职视图
CREATE OR REPLACE VIEW `hot_jobs_view` AS
SELECT
    j.id,
    j.title,
    j.salary,
    j.salary_unit,
    j.location,
    j.company_name,
    j.view_count,
    j.favorite_count,
    jc.name as category_name,
    ROW_NUMBER() OVER (PARTITION BY j.category ORDER BY (j.view_count * 0.4 + j.favorite_count * 0.6) DESC) as hot_rank
FROM `jobs` j
LEFT JOIN `job_categories` jc ON j.category = jc.code
WHERE j.status = 1
  AND j.is_deleted = 0
  AND (j.deadline IS NULL OR j.deadline > NOW());

-- 10. 添加外键约束（在创建所有表之后）
-- 注意：在生产环境中，根据性能和数据一致性需求决定是否启用外键约束
ALTER TABLE `jobs` ADD CONSTRAINT `fk_job_category` FOREIGN KEY (`category`) REFERENCES `job_categories` (`code`) ON DELETE SET NULL;

-- 11. 创建存储过程：更新兼职统计数据
DELIMITER //
DROP PROCEDURE IF EXISTS `sp_update_job_statistics`//
CREATE PROCEDURE `sp_update_job_statistics`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_publisher_id BIGINT;
    DECLARE v_job_id BIGINT;
    DECLARE cur CURSOR FOR
        SELECT DISTINCT publisher_id, id FROM jobs WHERE is_deleted = 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_publisher_id, v_job_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        INSERT INTO job_statistics
            (publisher_id, job_id, total_jobs, active_jobs, total_applications,
             pending_applications, approved_applications, rejected_applications,
             total_views, total_favorites, statistics_date, created_at)
        SELECT
            v_publisher_id,
            v_job_id,
            -- 统计逻辑
            (SELECT COUNT(*) FROM jobs j WHERE j.publisher_id = v_publisher_id AND j.is_deleted = 0) as total_jobs,
            (SELECT COUNT(*) FROM jobs j WHERE j.publisher_id = v_publisher_id AND j.status = 1 AND j.is_deleted = 0) as active_jobs,
            (SELECT COUNT(*) FROM job_applications ja WHERE ja.job_id = v_job_id AND ja.is_deleted = 0) as total_applications,
            (SELECT COUNT(*) FROM job_applications ja WHERE ja.job_id = v_job_id AND ja.status = 0 AND ja.is_deleted = 0) as pending_applications,
            (SELECT COUNT(*) FROM job_applications ja WHERE ja.job_id = v_job_id AND ja.status = 1 AND ja.is_deleted = 0) as approved_applications,
            (SELECT COUNT(*) FROM job_applications ja WHERE ja.job_id = v_job_id AND ja.status = 2 AND ja.is_deleted = 0) as rejected_applications,
            (SELECT COALESCE(SUM(view_count), 0) FROM jobs j WHERE j.id = v_job_id) as total_views,
            (SELECT COALESCE(SUM(favorite_count), 0) FROM jobs j WHERE j.id = v_job_id) as total_favorites,
            CURDATE(),
            NOW()
        ON DUPLICATE KEY UPDATE
            total_jobs = VALUES(total_jobs),
            active_jobs = VALUES(active_jobs),
            total_applications = VALUES(total_applications),
            pending_applications = VALUES(pending_applications),
            approved_applications = VALUES(approved_applications),
            rejected_applications = VALUES(rejected_applications),
            total_views = VALUES(total_views),
            total_favorites = VALUES(total_favorites),
            created_at = NOW();
    END LOOP;

    CLOSE cur;
END//
DELIMITER ;

-- 注意：实际部署时根据需要添加初始化数据