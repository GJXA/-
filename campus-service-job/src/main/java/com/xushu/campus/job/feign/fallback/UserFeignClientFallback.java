package com.xushu.campus.job.feign.fallback;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.job.feign.UserFeignClient;
import com.xushu.campus.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务Feign客户端降级处理（兼职服务使用）
 */
@Slf4j
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public Result<UserDTO> getUserById(Long userId) {
        log.warn("用户服务不可用，触发降级: getUserById, userId={}", userId);
        // 返回空用户信息，业务层需要处理这种情况
        return Result.success(new UserDTO());
    }

    @Override
    public Result<List<UserDTO>> getUsersByIds(List<Long> userIds) {
        log.warn("用户服务不可用，触发降级: getUsersByIds, userIds={}", userIds);
        // 返回空列表
        return Result.success(new ArrayList<>());
    }

}