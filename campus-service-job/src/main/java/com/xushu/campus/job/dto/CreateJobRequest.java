package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建兼职请求
 */
@Data
@Schema(description = "创建兼职请求")
public class CreateJobRequest {

    @NotBlank(message = "兼职标题不能为空")
    @Schema(description = "兼职标题", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String title;

    @NotBlank(message = "兼职描述不能为空")
    @Schema(description = "兼职描述", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 2000)
    private String description;

    @NotNull(message = "薪资不能为空")
    @Min(value = 0, message = "薪资必须大于等于0")
    @Schema(description = "薪资", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal salary;

    @NotBlank(message = "薪资单位不能为空")
    @Schema(description = "薪资单位: HOUR-时薪, DAY-日薪, MONTH-月薪, PROJECT-项目制", requiredMode = Schema.RequiredMode.REQUIRED)
    private String salaryUnit;

    @NotBlank(message = "工作类型不能为空")
    @Schema(description = "工作类型: FULL_TIME-全职, PART_TIME-兼职, INTERNSHIP-实习", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobType;

    @NotBlank(message = "工作类别不能为空")
    @Schema(description = "工作类别: SALES-销售, TUTOR-家教, WAITER-服务员, PROMOTION-促销, CLERK-文员, DESIGN-设计, PROGRAMMING-编程/IT, TRANSLATION-翻译, OTHER-其他", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    @NotBlank(message = "工作地点不能为空")
    @Schema(description = "工作地点", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 200)
    private String location;

    @Schema(description = "详细地址", maxLength = 500)
    private String address;

    @NotNull(message = "工作开始日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "工作开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @NotNull(message = "工作结束日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "工作结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    @Schema(description = "工作时间描述", maxLength = 200)
    private String workTime;

    @NotNull(message = "招聘人数不能为空")
    @Min(value = 1, message = "招聘人数必须大于0")
    @Schema(description = "招聘人数", requiredMode = Schema.RequiredMode.REQUIRED)
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

    @Schema(description = "截止时间（如果不填，默认为开始日期前7天）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate deadlineDate;

    @Schema(description = "是否置顶", defaultValue = "false")
    private Boolean isTop = false;

    @Schema(description = "是否推荐", defaultValue = "false")
    private Boolean isRecommended = false;

    @Schema(description = "发布者类型: USER-个人用户, COMPANY-企业用户, ADMIN-管理员", defaultValue = "USER")
    private String publisherType = "USER";

    /**
     * 验证薪资单位是否有效
     */
    public boolean isValidSalaryUnit() {
        return com.xushu.campus.job.constant.JobConstants.SalaryUnit.isValid(this.salaryUnit);
    }

    /**
     * 验证工作类型是否有效
     */
    public boolean isValidJobType() {
        return com.xushu.campus.job.constant.JobConstants.JobType.isValid(this.jobType);
    }

    /**
     * 验证工作类别是否有效
     */
    public boolean isValidCategory() {
        // 检查是否是有效的类别
        String[] validCategories = com.xushu.campus.job.constant.JobConstants.JobCategory.getAllCategories();
        for (String validCategory : validCategories) {
            if (validCategory.equals(this.category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证日期范围是否有效
     */
    public boolean isValidDateRange() {
        if (this.startDate == null || this.endDate == null) {
            return false;
        }
        return !this.endDate.isBefore(this.startDate);
    }
}