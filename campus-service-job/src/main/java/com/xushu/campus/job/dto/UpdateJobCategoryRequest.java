package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * 更新兼职分类请求
 */
@Data
@Schema(description = "更新兼职分类请求")
public class UpdateJobCategoryRequest {

    @Pattern(regexp = "^[A-Z_]+$", message = "分类编码必须是大写字母和下划线组成")
    @Schema(description = "分类编码（唯一标识，大写字母和下划线）", maxLength = 50)
    private String code;

    @Schema(description = "分类名称", maxLength = 100)
    private String name;

    @Schema(description = "分类描述", maxLength = 500)
    private String description;

    @Schema(description = "分类图标URL", maxLength = 200)
    private String icon;

    @Min(value = 0, message = "排序必须大于等于0")
    @Schema(description = "排序（数值越小越靠前）")
    private Integer sortOrder;

    @Schema(description = "是否启用：0-禁用，1-启用")
    private Integer enabled;

    /**
     * 验证分类编码格式（如果提供了的话）
     */
    public boolean isValidCode() {
        if (this.code == null) {
            return true;
        }
        return code.matches("^[A-Z_]+$");
    }

    /**
     * 验证启用状态是否有效（如果提供了的话）
     */
    public boolean isValidEnabled() {
        if (this.enabled == null) {
            return true;
        }
        return enabled == 0 || enabled == 1;
    }

    /**
     * 检查是否有字段需要更新
     */
    public boolean hasUpdates() {
        return this.code != null ||
                this.name != null ||
                this.description != null ||
                this.icon != null ||
                this.sortOrder != null ||
                this.enabled != null;
    }
}