package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新兼职请求
 */
@Data
@Schema(description = "更新兼职请求")
public class UpdateJobRequest {

    @Schema(description = "兼职标题", maxLength = 100)
    private String title;

    @Schema(description = "兼职描述", maxLength = 2000)
    private String description;

    @Schema(description = "薪资")
    @Min(value = 0, message = "薪资必须大于等于0")
    private BigDecimal salary;

    @Schema(description = "薪资单位: HOUR-时薪, DAY-日薪, MONTH-月薪, PROJECT-项目制")
    private String salaryUnit;

    @Schema(description = "工作类型: FULL_TIME-全职, PART_TIME-兼职, INTERNSHIP-实习")
    private String jobType;

    @Schema(description = "工作类别: SALES-销售, TUTOR-家教, WAITER-服务员, PROMOTION-促销, CLERK-文员, DESIGN-设计, PROGRAMMING-编程/IT, TRANSLATION-翻译, OTHER-其他")
    private String category;

    @Schema(description = "工作地点", maxLength = 200)
    private String location;

    @Schema(description = "详细地址", maxLength = 500)
    private String address;

    @Schema(description = "工作开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "工作结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "工作时间描述", maxLength = 200)
    private String workTime;

    @Schema(description = "招聘人数")
    @Min(value = 1, message = "招聘人数必须大于0")
    private Integer recruitCount;

    @Schema(description = "联系人姓名", maxLength = 50)
    private String contactName;

    @Schema(description = "联系人电话", maxLength = 20)
    private String contactPhone;

    @Schema(description = "联系人邮箱", maxLength = 100)
    private String contactEmail;

    @Schema(description = "企业/机构名称", maxLength = 100)
    private String companyName;

    @Schema(description = "企业描述", maxLength = 1000)
    private String companyDescription;

    @Schema(description = "企业Logo URL")
    private String companyLogo;

    @Schema(description = "职位要求", maxLength = 1000)
    private String requirements;

    @Schema(description = "福利待遇", maxLength = 1000)
    private String benefits;

    @Schema(description = "工作状态: 0-待审核, 1-招聘中, 2-已结束, 3-已关闭")
    private Integer status;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "置顶权重")
    private Integer topWeight;

    @Schema(description = "是否推荐")
    private Boolean isRecommended;

    @Schema(description = "审核备注", maxLength = 500)
    private String auditRemark;

    /**
     * 验证薪资单位是否有效（如果提供了的话）
     */
    public boolean isValidSalaryUnit() {
        if (this.salaryUnit == null) {
            return true;
        }
        return com.xushu.campus.job.constant.JobConstants.SalaryUnit.isValid(this.salaryUnit);
    }

    /**
     * 验证工作类型是否有效（如果提供了的话）
     */
    public boolean isValidJobType() {
        if (this.jobType == null) {
            return true;
        }
        return com.xushu.campus.job.constant.JobConstants.JobType.isValid(this.jobType);
    }

    /**
     * 验证工作类别是否有效（如果提供了的话）
     */
    public boolean isValidCategory() {
        if (this.category == null) {
            return true;
        }
        String[] validCategories = com.xushu.campus.job.constant.JobConstants.JobCategory.getAllCategories();
        for (String validCategory : validCategories) {
            if (validCategory.equals(this.category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证日期范围是否有效（如果提供了的话）
     */
    public boolean isValidDateRange() {
        if (this.startDate == null || this.endDate == null) {
            return true;
        }
        return !this.endDate.isBefore(this.startDate);
    }

    /**
     * 检查是否有字段需要更新
     */
    public boolean hasUpdates() {
        return this.title != null ||
                this.description != null ||
                this.salary != null ||
                this.salaryUnit != null ||
                this.jobType != null ||
                this.category != null ||
                this.location != null ||
                this.address != null ||
                this.startDate != null ||
                this.endDate != null ||
                this.workTime != null ||
                this.recruitCount != null ||
                this.contactName != null ||
                this.contactPhone != null ||
                this.contactEmail != null ||
                this.companyName != null ||
                this.companyDescription != null ||
                this.companyLogo != null ||
                this.requirements != null ||
                this.benefits != null ||
                this.status != null ||
                this.isTop != null ||
                this.topWeight != null ||
                this.isRecommended != null ||
                this.auditRemark != null;
    }
}