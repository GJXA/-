package com.xushu.campus.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 兼职统计数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("job_statistics")
public class JobStatistics {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布者ID
     */
    private Long publisherId;

    /**
     * 兼职ID
     */
    private Long jobId;

    /**
     * 总兼职数
     */
    private Integer totalJobs;

    /**
     * 活跃兼职数
     */
    private Integer activeJobs;

    /**
     * 总申请数
     */
    private Integer totalApplications;

    /**
     * 待处理申请数
     */
    private Integer pendingApplications;

    /**
     * 已通过申请数
     */
    private Integer approvedApplications;

    /**
     * 已拒绝申请数
     */
    private Integer rejectedApplications;

    /**
     * 总浏览量
     */
    private Integer totalViews;

    /**
     * 总收藏量
     */
    private Integer totalFavorites;

    /**
     * 统计日期
     */
    private LocalDate statisticsDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}