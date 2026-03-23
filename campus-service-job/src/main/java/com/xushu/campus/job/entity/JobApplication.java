package com.xushu.campus.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兼职申请实体类
 */
@Data
@TableName("job_applications")
public class JobApplication {

    /**
     * 申请记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 兼职ID
     */
    private Long jobId;

    /**
     * 申请人ID
     */
    private Long applicantId;


    /**
     * 申请人年级/专业
     */
    private String applicantGrade;

    /**
     * 申请人简历（简历文件路径或内容）
     */
    private String resume;

    /**
     * 申请状态：0-待处理，1-已通过，2-已拒绝，3-已取消
     */
    private Integer status;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

    /**
     * 处理人ID
     */
    private Long processorId;


    /**
     * 处理备注
     */
    private String processRemark;

    /**
     * 申请备注（用户填写）
     */
    private String applyRemark;

    /**
     * 面试时间
     */
    private LocalDateTime interviewTime;

    /**
     * 面试地点
     */
    private String interviewLocation;

    /**
     * 面试备注
     */
    private String interviewRemark;

    /**
     * 录用通知发送时间
     */
    private LocalDateTime offerTime;

    /**
     * 录用通知内容
     */
    private String offerContent;

    /**
     * 是否确认参加：0-未确认，1-确认参加，2-拒绝参加
     */
    private Integer confirmStatus;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认备注
     */
    private String confirmRemark;

    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 工作评价（由发布者填写）
     */
    private String workEvaluation;

    /**
     * 评价分数（1-5分）
     */
    private Integer evaluationScore;

    /**
     * 评价时间
     */
    private LocalDateTime evaluationTime;

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

    /**
     * 申请人姓名（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String applicantName;

    /**
     * 申请人电话（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String applicantPhone;

    /**
     * 申请人邮箱（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String applicantEmail;

    /**
     * 兼职标题（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String jobTitle;

    /**
     * 兼职薪资描述（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String jobSalary;

    /**
     * 兼职类型（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String jobType;

    /**
     * 兼职地点（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String jobLocation;

    /**
     * 发布者ID（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private Long publisherId;

    /**
     * 发布者姓名（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String publisherName;

    /**
     * 处理人姓名（瞬态字段，不存储到数据库）
     */
    @TableField(exist = false)
    private String processorName;

}