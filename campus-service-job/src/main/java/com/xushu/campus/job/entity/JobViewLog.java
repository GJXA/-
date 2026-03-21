package com.xushu.campus.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 兼职浏览记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("job_view_logs")
public class JobViewLog {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 兼职ID
     */
    private Long jobId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 浏览时间
     */
    private LocalDateTime viewedAt;

}