package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 兼职分类搜索请求
 */
@Data
@Schema(description = "兼职分类搜索请求")
public class JobCategorySearchRequest {

    @Schema(description = "关键词（编码、名称、描述模糊搜索）")
    private String keyword;

    @Schema(description = "是否启用：0-禁用，1-启用")
    private Integer enabled;

    @Schema(description = "排序字段：code, name, sort_order, create_time")
    private String sortField = "sort_order";

    @Schema(description = "排序方向：asc, desc", allowableValues = {"asc", "desc"})
    private String sortDirection = "asc";

    @Schema(description = "页码，从1开始", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页大小，默认20", defaultValue = "20")
    private Integer size = 20;

    /**
     * 验证排序字段是否有效
     */
    public boolean isValidSortField() {
        if (sortField == null) {
            return true;
        }
        return sortField.equals("code") ||
                sortField.equals("name") ||
                sortField.equals("sort_order") ||
                sortField.equals("create_time");
    }

    /**
     * 验证排序方向是否有效
     */
    public boolean isValidSortDirection() {
        if (sortDirection == null) {
            return true;
        }
        return sortDirection.equals("asc") || sortDirection.equals("desc");
    }

    /**
     * 获取偏移量（用于分页查询）
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}