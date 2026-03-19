package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 创建兼职分类请求
 */
@Data
@Schema(description = "创建兼职分类请求")
public class CreateJobCategoryRequest {

    @NotBlank(message = "分类编码不能为空")
    @Pattern(regexp = "^[A-Z_]+$", message = "分类编码必须是大写字母和下划线组成")
    @Schema(description = "分类编码（唯一标识，大写字母和下划线）", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String code;

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String name;

    @Schema(description = "分类描述", maxLength = 500)
    private String description;

    @Schema(description = "分类图标URL", maxLength = 200)
    private String icon;

    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序必须大于等于0")
    @Schema(description = "排序（数值越小越靠前）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sortOrder;

    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "1")
    private Integer enabled = 1;

    /**
     * 验证分类编码格式
     */
    public boolean isValidCode() {
        return code != null && code.matches("^[A-Z_]+$");
    }

    /**
     * 验证启用状态是否有效
     */
    public boolean isValidEnabled() {
        return enabled != null && (enabled == 0 || enabled == 1);
    }
}