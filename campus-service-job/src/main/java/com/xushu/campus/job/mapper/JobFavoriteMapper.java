package com.xushu.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.job.entity.JobFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 兼职收藏Mapper接口
 */
@Mapper
public interface JobFavoriteMapper extends BaseMapper<JobFavorite> {

    // 可以根据需要添加自定义查询方法

}