package com.xushu.campus.job.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兼职分类DTO
 */
@Data
@Schema(description = "兼职分类信息")
public class JobCategoryDTO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类编码（唯一标识）")
    private String code;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "是否启用：0-禁用，1-启用")
    private Integer enabled;

    @Schema(description = "是否启用描述")
    private String enabledDesc;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}