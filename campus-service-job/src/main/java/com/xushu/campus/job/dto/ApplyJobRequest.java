package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 申请兼职请求
 */
@Data
@Schema(description = "申请兼职请求")
public class ApplyJobRequest {

    @NotNull(message = "兼职ID不能为空")
    @Schema(description = "兼职ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(description = "申请人姓名", maxLength = 50)
    private String applicantName;

    @Schema(description = "申请人电话", maxLength = 20)
    private String applicantPhone;

    @Schema(description = "申请人邮箱", maxLength = 100)
    private String applicantEmail;

    @Schema(description = "申请人年级/专业", maxLength = 100)
    private String applicantGrade;

    @Schema(description = "简历（简历文件路径或内容）", maxLength = 1000)
    private String resume;

    @Schema(description = "申请备注", maxLength = 500)
    private String applyRemark;
}