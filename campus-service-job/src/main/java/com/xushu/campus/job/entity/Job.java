package com.xushu.campus.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 兼职信息实体类
 */
@Data
@TableName("jobs")
public class Job {

    /**
     * 兼职ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 兼职标题
     */
    private String title;

    /**
     * 兼职描述
     */
    private String description;

    /**
     * 薪资（单位：元/小时 或 元/天）
     */
    private BigDecimal salary;

    /**
     * 薪资单位：HOUR-时薪，DAY-日薪，MONTH-月薪，PROJECT-项目制
     */
    private String salaryUnit;

    /**
     * 工作类型：FULL_TIME-全职，PART_TIME-兼职，INTERNSHIP-实习
     */
    private String jobType;

    /**
     * 工作类别：SALES-销售，TUTOR-家教，WAITER-服务员，PROMOTION-促销，OTHER-其他
     */
    private String category;

    /**
     * 工作地点
     */
    private String location;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 工作开始日期
     */
    private LocalDate startDate;

    /**
     * 工作结束日期
     */
    private LocalDate endDate;

    /**
     * 工作时间（描述，如：周一至周五 9:00-18:00）
     */
    private String workTime;

    /**
     * 招聘人数
     */
    private Integer recruitCount;

    /**
     * 已申请人数
     */
    private Integer appliedCount;

    /**
     * 已录取人数
     */
    private Integer acceptedCount;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 企业/机构名称
     */
    private String companyName;

    /**
     * 企业描述
     */
    private String companyDescription;

    /**
     * 企业Logo
     */
    private String companyLogo;

    /**
     * 职位要求
     */
    private String requirements;

    /**
     * 福利待遇
     */
    private String benefits;

    /**
     * 工作状态：0-待审核，1-招聘中，2-已结束，3-已关闭
     */
    private Integer status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer favoriteCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 置顶权重（数值越大越靠前）
     */
    private Integer topWeight;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    /**
     * 发布者ID（企业用户或管理员）
     */
    private Long publisherId;

    /**
     * 发布者类型：USER-个人用户，COMPANY-企业用户，ADMIN-管理员
     */
    private String publisherType;

    /**
     * 发布者姓名（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String publisherName;


    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;

}