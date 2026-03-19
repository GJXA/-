package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 兼职统计数据DTO
 */
@Data
@Schema(description = "兼职统计数据")
public class JobStatisticsDTO {

    @Schema(description = "总兼职数量")
    private Integer totalJobs;

    @Schema(description = "招聘中的兼职数量")
    private Integer recruitingJobs;

    @Schema(description = "待审核的兼职数量")
    private Integer pendingJobs;

    @Schema(description = "已结束的兼职数量")
    private Integer endedJobs;

    @Schema(description = "已关闭的兼职数量")
    private Integer closedJobs;

    @Schema(description = "总申请人数")
    private Integer totalApplicants;

    @Schema(description = "待处理申请数量")
    private Integer pendingApplications;

    @Schema(description = "已通过申请数量")
    private Integer approvedApplications;

    @Schema(description = "已拒绝申请数量")
    private Integer rejectedApplications;

    @Schema(description = "总浏览次数")
    private Integer totalViews;

    @Schema(description = "总收藏次数")
    private Integer totalFavorites;

    @Schema(description = "平均薪资")
    private Double averageSalary;

    @Schema(description = "最高薪资")
    private Double maxSalary;

    @Schema(description = "最低薪资")
    private Double minSalary;

    @Schema(description = "最受欢迎的工作类别")
    private String mostPopularCategory;

    @Schema(description = "最受欢迎的工作类型")
    private String mostPopularJobType;

    @Schema(description = "即将过期的兼职数量（3天内）")
    private Integer aboutToExpireJobs;

    @Schema(description = "已过期但未关闭的兼职数量")
    private Integer expiredJobs;
}