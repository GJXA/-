package com.xushu.campus.user.controller;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.user.dto.CreateUserAddressRequest;
import com.xushu.campus.user.dto.UpdateUserAddressRequest;
import com.xushu.campus.user.dto.UserAddressDTO;
import com.xushu.campus.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户地址控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user-addresses")
@RequiredArgsConstructor
@Tag(name = "用户地址管理", description = "用户地址的增删改查接口")
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping
    @Operation(summary = "创建用户地址", description = "为当前登录用户创建新地址")
    public Result<UserAddressDTO> createAddress(@Valid @RequestBody CreateUserAddressRequest request,
                                                HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null) {
            return Result.unauthorized("用户未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        log.info("创建用户地址请求: userId={}", userId);
        UserAddressDTO addressDTO = userAddressService.createAddress(userId, request);
        return Result.success("地址创建成功", addressDTO);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "更新用户地址", description = "更新指定地址信息")
    public Result<UserAddressDTO> updateAddress(@PathVariable @Parameter(description = "地址ID") Long addressId,
                                                @Valid @RequestBody UpdateUserAddressRequest request,
                                                HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null) {
            return Result.unauthorized("用户未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        log.info("更新用户地址请求: userId={}, addressId={}", userId, addressId);
        UserAddressDTO addressDTO = userAddressService.updateAddress(userId, addressId, request);
        return Result.success("地址更新成功", addressDTO);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "删除用户地址", description = "删除指定地址（逻辑删除）")
    public Result<String> deleteAddress(@PathVariable @Parameter(description = "地址ID") Long addressId,
                                        HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null) {
            return Result.unauthorized("用户未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        log.info("删除用户地址请求: userId={}, addressId={}", userId, addressId);
        userAddressService.deleteAddress(userId, addressId);
        return Result.success("地址删除成功");
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "获取地址详情", description = "根据地址ID获取地址详情")
    public Result<UserAddressDTO> getAddressById(@PathVariable @Parameter(description = "地址ID") Long addressId,
                                                 HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null) {
            return Result.unauthorized("用户未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        log.debug("获取地址详情请求: userId={}, addressId={}", userId, addressId);
        UserAddressDTO addressDTO = userAddressService.getAddressById(userId, addressId);
        return Result.success(addressDTO);
    }

    @GetMapping
    @Operation(summary = "获取用户地址列表", description = "获取当前登录用户的所有地址列表")
    public Result<List<UserAddressDTO>> getUserAddresses(HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null) {
            return Result.unauthorized("用户未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        log.debug("获取用户地址列表请求: userId={}", userId);
        List<UserAddressDTO> addressList = userAddressService.getUserAddresses(userId);
        return Result.success(addressList);
    }

    @PutMapping("/{addressId}/set-default")
    @Operation(summary = "设置为默认地址", description = "将指定地址设置为用户的默认地址")
    public Result<String> setDefaultAddress(@PathVariable @Parameter(description = "地址ID") Long addressId,
                                            HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null) {
            return Result.unauthorized("用户未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        log.info("设置为默认地址请求: userId={}, addressId={}", userId, addressId);
        userAddressService.setDefaultAddress(userId, addressId);
        return Result.success("默认地址设置成功");
    }

}