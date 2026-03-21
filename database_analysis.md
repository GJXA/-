# 数据库表结构冗余分析与优化建议

## 1. 用户服务 (campus_user)

### 1.1 users 表 (当前字段: 23个)
**字段列表:**
1. `id` - 主键
2. `username` - 用户名
3. `password` - 密码
4. `email` - 邮箱
5. `phone` - 手机号
6. `real_name` - 真实姓名
7. `student_id` - 学号
8. `avatar_url` - 头像
9. `gender` - 性别
10. `birthday` - 生日
11. `school` - 学校
12. `major` - 专业
13. `grade` - 年级
14. `address` - 地址
15. `signature` - 个性签名
16. `status` - 状态
17. `last_login_time` - 最后登录时间
18. `last_login_ip` - 最后登录IP
19. `deleted` - 逻辑删除
20. `create_time` - 创建时间
21. `update_time` - 更新时间
22. `version` - 版本号

**冗余字段分析:**
- ✅ **无显著冗余字段**: 所有字段都是必要的用户基本信息
- ⚠️ `last_login_ip` - 使用频率较低，但保留对安全审计有帮助

**优化建议:**
- 保持现有结构，设计合理

### 1.2 其他表分析
- `user_roles`: 无冗余
- `user_auth_logs`: 无冗余
- `user_favorites`: 无冗余
- `user_addresses`: 无冗余
- `user_wallets`: 无冗余

## 2. 商品服务 (campus_product)

### 2.1 products 表 (当前字段: 30个)
**字段列表:**
1. `id` - 商品ID
2. `user_id` - 卖家ID
3. `title` - 商品标题
4. `description` - 商品描述
5. `price` - 价格
6. `original_price` - 原价
7. `category_id` - 分类ID
8. `status` - 状态
9. `view_count` - 浏览次数
10. `like_count` - 点赞数
11. `favorite_count` - 收藏数
12. `location` - 位置
13. `address` - 详细地址
14. `contact_phone` - 联系电话
15. `contact_name` - 联系人姓名
16. `quality_level` - 成色
17. `publish_time` - 发布时间
18. `expire_time` - 过期时间
19. `is_top` - 是否置顶
20. `top_weight` - 置顶权重
21. `is_recommended` - 是否推荐
22. `auditor_id` - 审核人ID
23. `audit_time` - 审核时间
24. `audit_remark` - 审核备注
25. `sold_time` - 售出时间
26. `buyer_id` - 买家ID
27. `deleted` - 逻辑删除
28. `create_time` - 创建时间
29. `update_time` - 更新时间
30. `version` - 版本号

**冗余字段分析:**
1. 🔴 **可计算字段** (建议保留为缓存):
   - `view_count`, `like_count`, `favorite_count` - 可通过统计表计算，但作为缓存字段提升性能
2. ⚠️ **可能冗余字段**:
   - `location` 和 `address` - 功能重复，建议合并为 `address`，添加 `location` 作为地理坐标或区域编码
   - `contact_phone`, `contact_name` - 可从用户表获取，但商品可能需要独立联系方式
3. 🔴 **业务冗余字段**:
   - `auditor_id`, `audit_time`, `audit_remark` - 可移至独立的审核记录表
   - `sold_time`, `buyer_id` - 可移至订单/交易表

**精简后表结构建议 (26个字段):**
```sql
CREATE TABLE IF NOT EXISTS `products` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '卖家ID',
  `title` varchar(200) NOT NULL COMMENT '商品标题',
  `description` text COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) COMMENT '原价',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-待审核，1-上架，2-下架，3-已售出',
  `view_count` int DEFAULT 0 COMMENT '浏览次数（缓存）',
  `like_count` int DEFAULT 0 COMMENT '点赞数（缓存）',
  `favorite_count` int DEFAULT 0 COMMENT '收藏数（缓存）',
  `address` varchar(300) COMMENT '详细地址（包含位置信息）',
  `contact_phone` varchar(20) COMMENT '联系电话',
  `contact_name` varchar(50) COMMENT '联系人姓名',
  `quality_level` varchar(20) COMMENT '成色：NEW-全新，LIKE_NEW-几乎全新，USED-使用过',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `expire_time` datetime COMMENT '过期时间',
  `is_top` tinyint DEFAULT 0 COMMENT '是否置顶',
  `top_weight` int DEFAULT 0 COMMENT '置顶权重',
  `is_recommended` tinyint DEFAULT 0 COMMENT '是否推荐',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT 0 COMMENT '版本号（乐观锁）',
  INDEX idx_user_id(`user_id`),
  INDEX idx_category_id(`category_id`),
  INDEX idx_status(`status`),
  INDEX idx_publish_time(`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
```

**迁移建议:**
1. 移除 `location` 字段，将其信息合并到 `address`
2. 移除 `auditor_id`, `audit_time`, `audit_remark`，创建独立的 `product_audit_logs` 表
3. 移除 `sold_time`, `buyer_id`，这些信息应从订单表获取

### 2.2 其他表分析
- `product_categories`: 无冗余
- `product_images`: 无冗余
- `product_view_logs`: 无冗余
- `product_likes`: 无冗余
- `product_comments`: 无冗余
- `product_reports`: 无冗余
- `product_statistics`: 无冗余

## 3. 订单服务 (campus_order)

### 3.1 orders 表 (当前字段: 31个)
**字段列表:**
1. `id` - 订单ID
2. `order_no` - 订单号
3. `user_id` - 买家ID
4. `product_id` - 商品ID
5. `product_title` - 商品标题
6. `product_price` - 商品价格
7. `product_image` - 商品图片
8. `quantity` - 数量
9. `total_amount` - 总金额
10. `pay_amount` - 支付金额
11. `payment_method` - 支付方式
12. `payment_status` - 支付状态
13. `order_status` - 订单状态
14. `receiver_name` - 收货人姓名
15. `receiver_phone` - 收货人电话
16. `receiver_address` - 收货地址
17. `buyer_message` - 买家留言
18. `seller_message` - 卖家留言
19. `payment_time` - 支付时间
20. `delivery_time` - 发货时间
21. `confirm_time` - 确认收货时间
22. `cancel_time` - 取消时间
23. `cancel_reason` - 取消原因
24. `expire_time` - 过期时间
25. `seller_id` - 卖家ID
26. `seller_nickname` - 卖家昵称
27. `buyer_nickname` - 买家昵称
28. `deleted` - 逻辑删除
29. `create_time` - 创建时间
30. `update_time` - 更新时间
31. `version` - 版本号

**冗余字段分析:**
1. 🔴 **订单快照字段** (建议保留):
   - `product_title`, `product_price`, `product_image` - 订单需要商品快照，合理冗余
2. 🔴 **用户昵称冗余** (建议移除):
   - `seller_nickname`, `buyer_nickname` - 可通过用户ID查询获取，移除以减少冗余
3. ✅ **其他字段**: 均为必要业务字段

**精简后表结构建议 (29个字段):**
```sql
CREATE TABLE IF NOT EXISTS `orders` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` varchar(64) NOT NULL COMMENT '订单号',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID（买家）',
    `product_id` bigint(20) NOT NULL COMMENT '商品ID',
    `product_title` varchar(255) NOT NULL COMMENT '商品标题（快照）',
    `product_price` decimal(10,2) NOT NULL COMMENT '商品价格（快照）',
    `product_image` varchar(500) COMMENT '商品图片（快照）',
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
    KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
```

### 3.2 其他表分析
- `order_operation_log`: 无冗余
- `order_refund`: 无冗余
- `order_delivery`: 无冗余

## 4. 兼职服务 (campus_job)

### 4.1 jobs 表 (当前字段: 41个)
**字段列表:**
1. `id` - 兼职ID
2. `title` - 标题
3. `description` - 描述
4. `salary` - 薪资
5. `salary_unit` - 薪资单位
6. `job_type` - 工作类型
7. `category` - 类别
8. `location` - 地点
9. `address` - 地址
10. `start_date` - 开始日期
11. `end_date` - 结束日期
12. `work_time` - 工作时间
13. `recruit_count` - 招聘人数
14. `applied_count` - 已申请数
15. `accepted_count` - 已录取数
16. `contact_name` - 联系人
17. `contact_phone` - 联系电话
18. `contact_email` - 联系邮箱
19. `company_name` - 公司名称
20. `company_description` - 公司描述
21. `company_logo` - 公司Logo
22. `requirements` - 要求
23. `benefits` - 福利
24. `status` - 状态
25. `view_count` - 浏览次数
26. `favorite_count` - 收藏次数
27. `publish_time` - 发布时间
28. `deadline` - 截止时间
29. `is_top` - 是否置顶
30. `top_weight` - 置顶权重
31. `is_recommended` - 是否推荐
32. `publisher_id` - 发布者ID
33. `publisher_type` - 发布者类型
34. `publisher_name` - 发布者名称
35. `auditor_id` - 审核人ID
36. `audit_time` - 审核时间
37. `audit_remark` - 审核备注
38. `deleted` - 逻辑删除
39. `create_time` - 创建时间
40. `update_time` - 更新时间
41. `version` - 版本号

**冗余字段分析:**
1. 🔴 **统计缓存字段** (建议保留):
   - `applied_count`, `accepted_count`, `view_count`, `favorite_count`
2. 🔴 **发布者信息冗余**:
   - `publisher_name` - 可从用户表获取
3. 🔴 **联系信息可能冗余**:
   - `contact_name`, `contact_phone`, `contact_email` - 可能通过发布者获取
4. 🔴 **审核信息冗余**:
   - `auditor_id`, `audit_time`, `audit_remark` - 可移至审核记录表
5. ⚠️ **位置信息重复**:
   - `location` 和 `address` - 功能相似

**精简后表结构建议 (35个字段):**
```sql
CREATE TABLE IF NOT EXISTS `jobs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '兼职ID',
  `title` varchar(200) NOT NULL COMMENT '兼职标题',
  `description` text COMMENT '兼职描述',
  `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资',
  `salary_unit` varchar(20) DEFAULT NULL COMMENT '薪资单位：HOUR-时薪，DAY-日薪，MONTH-月薪，PROJECT-项目制',
  `job_type` varchar(20) DEFAULT NULL COMMENT '工作类型：FULL_TIME-全职，PART_TIME-兼职，INTERNSHIP-实习',
  `category` varchar(20) DEFAULT NULL COMMENT '工作类别编码',
  `address` varchar(300) DEFAULT NULL COMMENT '工作地址（包含位置信息）',
  `start_date` date DEFAULT NULL COMMENT '工作开始日期',
  `end_date` date DEFAULT NULL COMMENT '工作结束日期',
  `work_time` varchar(100) DEFAULT NULL COMMENT '工作时间描述',
  `recruit_count` int DEFAULT '1' COMMENT '招聘人数',
  `applied_count` int DEFAULT '0' COMMENT '已申请人数（缓存）',
  `accepted_count` int DEFAULT '0' COMMENT '已录取人数（缓存）',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系人电话',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系人邮箱',
  `company_name` varchar(100) DEFAULT NULL COMMENT '企业/机构名称',
  `company_description` text COMMENT '企业描述',
  `company_logo` varchar(500) DEFAULT NULL COMMENT '企业Logo',
  `requirements` text COMMENT '职位要求',
  `benefits` text COMMENT '福利待遇',
  `status` int DEFAULT '0' COMMENT '工作状态：0-待审核，1-招聘中，2-已结束，3-已关闭',
  `view_count` int DEFAULT '0' COMMENT '浏览次数（缓存）',
  `favorite_count` int DEFAULT '0' COMMENT '收藏次数（缓存）',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `top_weight` int DEFAULT '0' COMMENT '置顶权重',
  `is_recommended` tinyint(1) DEFAULT '0' COMMENT '是否推荐',
  `publisher_id` bigint NOT NULL COMMENT '发布者ID',
  `publisher_type` varchar(20) DEFAULT NULL COMMENT '发布者类型：USER-个人用户，COMPANY-企业用户，ADMIN-管理员',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_status` (`status`),
  KEY `idx_category` (`category`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职信息表';
```

### 4.2 job_applications 表冗余分析
**显著冗余字段:**
- `job_title`, `job_salary`, `job_type`, `job_location`, `publisher_id`, `publisher_name` - 这些字段完全可以从jobs表关联获取

**建议:** 移除这些冗余字段，简化表结构

## 5. 通知服务 (campus_notification)

### 5.1 notifications 表 (当前字段: 39个)
**冗余字段分析:**
1. 🔴 **多种发送状态字段**:
   - `email_status`, `email_failure_reason`, `email_retry_count`
   - `sms_status`, `sms_failure_reason`, `sms_retry_count`
   - `websocket_status`
   - 建议: 创建独立的 `notification_delivery_logs` 表记录发送状态
2. 🔴 **接收人联系信息冗余**:
   - `receiver_email`, `receiver_phone` - 可从用户表获取
3. 🔴 **发送人信息冗余**:
   - `sender_name` - 可从用户表获取

**精简建议:** 移除上述冗余字段，表字段从39个减少到约25个

## 6. 字段命名优化建议

### 6.1 统一命名风格
当前命名已基本符合MySQL小写字母+下划线规范，但存在一些不一致:

1. **表名复数问题**:
   - `users` ✓
   - `products` ✓
   - `orders` ✓
   - `jobs` ✓ (应改为 `job_postings` 更明确)
   - `notifications` ✓

2. **字段名长度不一致**:
   - `student_id` ✓
   - `avatar_url` ✓
   - 但有些字段如 `quality_level` 可以简化为 `quality`

3. **布尔字段命名**:
   - `is_top`, `is_recommended` ✓ (使用 `is_` 前缀明确)
   - `deleted` ✗ (建议改为 `is_deleted` 保持一致性)

### 6.2 具体优化建议
1. **所有表统一添加 `is_` 前缀的布尔字段**:
   - `deleted` → `is_deleted`
   - `enabled` → `is_enabled`
   - `is_top`, `is_recommended` 保持原样

2. **简化过长字段名**:
   - `quality_level` → `quality`
   - `salary_unit` → `salary_unit` (保持)
   - `application_status` → `status` (保持)

3. **统一外键字段命名**:
   - `user_id` ✓
   - `category_id` ✓
   - `product_id` ✓
   - 保持 `[表名单数]_id` 模式

4. **时间字段命名**:
   - `create_time`, `update_time` ✓
   - `publish_time` ✓
   - `audit_time` → `reviewed_at` (更语义化)

### 6.3 推荐命名规范
```sql
-- 布尔字段
is_active, is_deleted, is_verified, is_top, is_recommended

-- 时间字段
created_at, updated_at, published_at, expires_at, deleted_at

-- 状态字段
status (使用枚举值), state (有限状态机)

-- 外键字段
user_id, product_id, category_id, parent_id

-- 数量字段
view_count, like_count, favorite_count, total_amount
```

## 7. 实施建议

### 7.1 优先级
1. **高优先级**: 移除明显的冗余字段（如job_applications中的冗余字段）
2. **中优先级**: 统一字段命名规范
3. **低优先级**: 拆分审核、发送状态等辅助表

### 7.2 迁移策略
1. 创建新表结构
2. 编写数据迁移脚本
3. 分批次迁移，确保数据一致性
4. 更新应用程序代码
5. 验证功能完整性

### 7.3 风险控制
- 保持向后兼容性，逐步迁移
- 重要数据字段先保留，确认无影响后再移除
- 充分测试所有业务场景