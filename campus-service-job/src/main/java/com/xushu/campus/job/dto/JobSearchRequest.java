package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 兼职搜索请求
 */
@Data
@Schema(description = "兼职搜索请求")
public class JobSearchRequest {

    @Schema(description = "关键词（标题、描述、公司名称等模糊搜索）")
    private String keyword;

    @Schema(description = "工作类型: FULL_TIME-全职, PART_TIME-兼职, INTERNSHIP-实习")
    private String jobType;

    @Schema(description = "工作类别: SALES-销售, TUTOR-家教, WAITER-服务员, PROMOTION-促销, CLERK-文员, DESIGN-设计, PROGRAMMING-编程/IT, TRANSLATION-翻译, OTHER-其他")
    private String category;

    @Schema(description = "薪资单位: HOUR-时薪, DAY-日薪, MONTH-月薪, PROJECT-项目制")
    private String salaryUnit;

    @Schema(description = "最低薪资")
    private BigDecimal minSalary;

    @Schema(description = "最高薪资")
    private BigDecimal maxSalary;

    @Schema(description = "工作地点（模糊搜索）")
    private String location;

    @Schema(description = "工作状态: 0-待审核, 1-招聘中, 2-已结束, 3-已关闭")
    private Integer status;

    @Schema(description = "发布者ID")
    private Long publisherId;

    @Schema(description = "发布者类型: USER-个人用户, COMPANY-企业用户, ADMIN-管理员")
    private String publisherType;

    @Schema(description = "开始日期之后（查找开始日期在此之后的兼职）")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateAfter;

    @Schema(description = "结束日期之前（查找结束日期在此之前的兼职）")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateBefore;

    @Schema(description = "发布时间之后")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTimeAfter;

    @Schema(description = "发布时间之前")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTimeBefore;

    @Schema(description = "截止时间之后")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadlineAfter;

    @Schema(description = "截止时间之前")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadlineBefore;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "是否推荐")
    private Boolean isRecommended;

    @Schema(description = "是否有剩余名额（true-只显示有剩余名额的兼职）")
    private Boolean hasRemaining;

    @Schema(description = "是否即将过期（true-只显示3天内即将过期的兼职）")
    private Boolean aboutToExpire;

    @Schema(description = "页码，默认1")
    private Integer page = 1;

    @Schema(description = "每页大小，默认20")
    private Integer size = 20;

    @Schema(description = "排序字段，默认publish_time", allowableValues = {"publish_time", "salary", "deadline", "view_count", "applied_count", "top_weight"})
    private String sortField = "publish_time";

    @Schema(description = "排序方向，默认desc", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";

    /**
     * 验证搜索参数是否有效
     */
    public boolean isValid() {
        // 验证薪资范围
        if (this.minSalary != null && this.maxSalary != null) {
            if (this.minSalary.compareTo(this.maxSalary) > 0) {
                return false;
            }
        }

        // 验证日期范围
        if (this.startDateAfter != null && this.endDateBefore != null) {
            if (this.endDateBefore.isBefore(this.startDateAfter)) {
                return false;
            }
        }

        // 验证时间范围
        if (this.publishTimeAfter != null && this.publishTimeBefore != null) {
            if (this.publishTimeBefore.isBefore(this.publishTimeAfter)) {
                return false;
            }
        }

        if (this.deadlineAfter != null && this.deadlineBefore != null) {
            if (this.deadlineBefore.isBefore(this.deadlineAfter)) {
                return false;
            }
        }

        // 验证工作类型
        if (this.jobType != null && !com.xushu.campus.job.constant.JobConstants.JobType.isValid(this.jobType)) {
            return false;
        }

        // 验证薪资单位
        if (this.salaryUnit != null && !com.xushu.campus.job.constant.JobConstants.SalaryUnit.isValid(this.salaryUnit)) {
            return false;
        }

        // 验证页码和大小
        if (this.page != null && this.page < 1) {
            return false;
        }
        if (this.size != null && (this.size < 1 || this.size > 100)) {
            return false;
        }

        return true;
    }

    /**
     * 检查是否有搜索条件
     */
    public boolean hasSearchCriteria() {
        return this.keyword != null ||
                this.jobType != null ||
                this.category != null ||
                this.salaryUnit != null ||
                this.minSalary != null ||
                this.maxSalary != null ||
                this.location != null ||
                this.status != null ||
                this.publisherId != null ||
                this.publisherType != null ||
                this.startDateAfter != null ||
                this.endDateBefore != null ||
                this.publishTimeAfter != null ||
                this.publishTimeBefore != null ||
                this.deadlineAfter != null ||
                this.deadlineBefore != null ||
                this.isTop != null ||
                this.isRecommended != null ||
                this.hasRemaining != null ||
                this.aboutToExpire != null;
    }
}