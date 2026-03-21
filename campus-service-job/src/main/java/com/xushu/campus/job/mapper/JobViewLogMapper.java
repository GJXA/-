package com.xushu.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.job.entity.JobViewLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 兼职浏览记录Mapper接口
 */
@Mapper
public interface JobViewLogMapper extends BaseMapper<JobViewLog> {

    // 可以根据需要添加自定义查询方法

}