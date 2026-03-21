package com.xushu.campus.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.user.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户收藏Mapper接口
 */
@Mapper
@Repository
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    // 可以根据需要添加自定义查询方法

}