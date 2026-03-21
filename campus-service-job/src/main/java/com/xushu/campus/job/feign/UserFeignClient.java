package com.xushu.campus.job.feign;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.job.feign.fallback.UserFeignClientFallback;
import com.xushu.campus.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户服务Feign客户端（兼职服务使用）
 */
@FeignClient(name = "campus-service-user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/api/users/{userId}")
    Result<UserDTO> getUserById(@PathVariable("userId") Long userId);

    /**
     * 根据用户ID列表批量获取用户信息
     */
    @PostMapping("/api/users/batch")
    Result<List<UserDTO>> getUsersByIds(@RequestBody List<Long> userIds);

}