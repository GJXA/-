package com.xushu.campus.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户钱包实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_wallets")
public class UserWallet {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}