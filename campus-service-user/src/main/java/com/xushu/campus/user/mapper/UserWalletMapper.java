package com.xushu.campus.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.user.entity.UserWallet;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户钱包Mapper接口
 */
@Mapper
@Repository
public interface UserWalletMapper extends BaseMapper<UserWallet> {

    // 可以根据需要添加自定义查询方法

}