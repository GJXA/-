package com.xushu.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.job.entity.JobStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 兼职统计Mapper接口
 */
@Mapper
public interface JobStatisticsMapper extends BaseMapper<JobStatistics> {

    // 可以根据需要添加自定义查询方法

}