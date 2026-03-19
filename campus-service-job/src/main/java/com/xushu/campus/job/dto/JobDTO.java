package com.xushu.campus.job.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 兼职信息DTO
 */
@Data
@Schema(description = "兼职信息")
public class JobDTO {

    @Schema(description = "兼职ID")
    private Long id;

    @Schema(description = "兼职标题")
    private String title;

    @Schema(description = "兼职描述")
    private String description;

    @Schema(description = "薪资")
    private BigDecimal salary;

    @Schema(description = "薪资单位")
    private String salaryUnit;

    @Schema(description = "薪资单位描述")
    private String salaryUnitDesc;

    @Schema(description = "工作类型")
    private String jobType;

    @Schema(description = "工作类型描述")
    private String jobTypeDesc;

    @Schema(description = "工作类别")
    private String category;

    @Schema(description = "工作类别描述")
    private String categoryDesc;

    @Schema(description = "工作地点")
    private String location;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "工作开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "工作结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "工作时间")
    private String workTime;

    @Schema(description = "招聘人数")
    private Integer recruitCount;

    @Schema(description = "已申请人数")
    private Integer appliedCount;

    @Schema(description = "已录取人数")
    private Integer acceptedCount;

    @Schema(description = "剩余名额")
    private Integer remainingCount;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "联系人邮箱")
    private String contactEmail;

    @Schema(description = "企业/机构名称")
    private String companyName;

    @Schema(description = "企业描述")
    private String companyDescription;

    @Schema(description = "企业Logo")
    private String companyLogo;

    @Schema(description = "职位要求")
    private String requirements;

    @Schema(description = "福利待遇")
    private String benefits;

    @Schema(description = "工作状态")
    private Integer status;

    @Schema(description = "工作状态描述")
    private String statusDesc;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "收藏次数")
    private Integer favoriteCount;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "置顶权重")
    private Integer topWeight;

    @Schema(description = "是否推荐")
    private Boolean isRecommended;

    @Schema(description = "发布者ID")
    private Long publisherId;

    @Schema(description = "发布者类型")
    private String publisherType;

    @Schema(description = "发布者类型描述")
    private String publisherTypeDesc;

    @Schema(description = "发布者名称")
    private String publisherName;

    @Schema(description = "审核人ID")
    private Long auditorId;

    @Schema(description = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 计算状态描述和剩余名额
     */
    public void calculateDesc() {
        if (this.salaryUnit != null) {
            this.salaryUnitDesc = com.xushu.campus.job.constant.JobConstants.SalaryUnit.getDesc(this.salaryUnit);
        }
        if (this.jobType != null) {
            this.jobTypeDesc = com.xushu.campus.job.constant.JobConstants.JobType.getDesc(this.jobType);
        }
        if (this.category != null) {
            this.categoryDesc = com.xushu.campus.job.constant.JobConstants.JobCategory.getDesc(this.category);
        }
        if (this.status != null) {
            this.statusDesc = com.xushu.campus.job.constant.JobConstants.JobStatus.getDesc(this.status);
        }
        if (this.publisherType != null) {
            this.publisherTypeDesc = com.xushu.campus.job.constant.JobConstants.PublisherType.getDesc(this.publisherType);
        }

        // 计算剩余名额
        if (this.recruitCount != null && this.acceptedCount != null) {
            this.remainingCount = Math.max(0, this.recruitCount - this.acceptedCount);
        } else if (this.recruitCount != null) {
            this.remainingCount = this.recruitCount;
        } else {
            this.remainingCount = 0;
        }
    }

    /**
     * 检查是否可以申请
     */
    public boolean canApply() {
        if (this.status == null) {
            return false;
        }
        // 只有招聘中的职位可以申请，并且还有剩余名额
        return com.xushu.campus.job.constant.JobConstants.JobStatus.canApply(this.status) &&
                (this.remainingCount == null || this.remainingCount > 0);
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        if (this.deadline == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.deadline);
    }

    /**
     * 检查是否即将过期（3天内过期）
     */
    public boolean isAboutToExpire() {
        if (this.deadline == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);
        return now.isBefore(this.deadline) && threeDaysLater.isAfter(this.deadline);
    }
}