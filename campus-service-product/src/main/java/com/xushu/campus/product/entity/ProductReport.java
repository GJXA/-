package com.xushu.campus.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品举报实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_reports")
public class ProductReport {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 举报人ID
     */
    private Long reporterId;

    /**
     * 举报类型：FAKE-虚假信息，ILLEGAL-违法信息，OTHER-其他
     */
    private String reportType;

    /**
     * 举报原因
     */
    private String reportReason;

    /**
     * 证据图片（JSON数组）
     */
    private String evidenceImages;

    /**
     * 状态：0-待处理，1-处理中，2-已处理，3-已驳回
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long processorId;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

    /**
     * 处理结果
     */
    private String processResult;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}