package com.xushu.campus.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品统计数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_statistics")
public class ProductStatistics {

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
     * 分类ID
     */
    private Long categoryId;

    /**
     * 总浏览量
     */
    private Integer totalViews;

    /**
     * 总点赞数
     */
    private Integer totalLikes;

    /**
     * 总收藏数
     */
    private Integer totalFavorites;

    /**
     * 总评论数
     */
    private Integer totalComments;

    /**
     * 总销量
     */
    private Integer totalSales;

    /**
     * 统计日期
     */
    private LocalDate statisticsDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}