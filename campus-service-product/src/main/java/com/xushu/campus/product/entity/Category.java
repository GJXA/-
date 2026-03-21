package com.xushu.campus.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_categories")
public class Category {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类编码（唯一标识）
     */
    private String code;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否启用：0-禁用，1-启用
     */
    @TableField("is_enabled")
    private Integer isEnabled;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updateTime;

    /**
     * 状态（瞬态字段，对应isEnabled）
     */
    @TableField(exist = false)
    private Integer status;

    /**
     * 层级（瞬态字段，0-根分类，1-子分类）
     */
    @TableField(exist = false)
    private Integer level;

    /**
     * 获取状态（兼容旧代码）
     */
    public Integer getStatus() {
        return isEnabled;
    }

    /**
     * 设置状态（兼容旧代码）
     */
    public void setStatus(Integer status) {
        this.isEnabled = status;
        this.status = status;
    }

    /**
     * 获取层级（兼容旧代码）
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 设置层级（兼容旧代码）
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 获取排序（兼容旧代码）
     */
    public Integer getSort() {
        return sortOrder;
    }

    /**
     * 设置排序（兼容旧代码）
     */
    public void setSort(Integer sort) {
        this.sortOrder = sort;
    }

}