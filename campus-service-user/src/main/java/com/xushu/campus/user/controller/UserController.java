package com.xushu.campus.user.controller;

import com.xushu.campus.common.model.Result;
import com.xushu.campus.user.dto.*;
import com.xushu.campus.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息管理接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: username={}", request.getUsername());
        UserDTO userDTO = userService.register(request);
        return Result.success("注册成功", userDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录请求: username={}", request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginResponse> refreshToken(@RequestParam @NotBlank String refreshToken) {
        log.info("刷新令牌请求");
        LoginResponse response = userService.refreshToken(refreshToken);
        return Result.success("令牌刷新成功", response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public Result<UserDTO> getUserById(
            @PathVariable @Parameter(description = "用户ID") Long userId) {
        log.debug("获取用户信息请求: userId={}", userId);
        UserDTO userDTO = userService.getUserById(userId);
        return Result.success(userDTO);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名获取用户信息", description = "根据用户名获取用户信息")
    public Result<UserDTO> getUserByUsername(
            @PathVariable @Parameter(description = "用户名") String username) {
        log.debug("根据用户名获取用户信息请求: username={}", username);
        UserDTO userDTO = userService.getUserByUsername(username);
        return Result.success(userDTO);
    }

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public Result<UserDTO> getCurrentUser(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }

        log.debug("获取当前用户信息请求: userId={}", userId);
        UserDTO userDTO = userService.getUserById(Long.parseLong(userId));
        return Result.success(userDTO);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的个人信息")
    public Result<UserDTO> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request,
                                             HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null) {
            return Result.unauthorized("用户未登录");
        }

        Long userId = Long.parseLong(userIdStr);
        log.info("更新当前用户信息请求: userId={}", userId);
        UserDTO userDTO = userService.updateUser(userId, request);
        return Result.success("更新成功", userDTO);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "更新用户个人信息")
    public Result<UserDTO> updateUser(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("更新用户信息请求: userId={}", userId);
        UserDTO userDTO = userService.updateUser(userId, request);
        return Result.success("更新成功", userDTO);
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    public Result<String> changePassword(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam @NotBlank String oldPassword,
            @RequestParam @NotBlank String newPassword) {
        log.info("修改密码请求: userId={}", userId);
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "通过邮箱重置密码")
    public Result<String> resetPassword(
            @RequestParam @NotBlank String username,
            @RequestParam @NotBlank String email) {
        log.info("重置密码请求: username={}, email={}", username, email);
        userService.resetPassword(username, email);
        return Result.success("密码重置成功，请查收邮件");
    }

    @PutMapping("/{userId}/disable")
    @Operation(summary = "禁用用户", description = "禁用指定用户（管理员权限）")
    public Result<String> disableUser(
            @PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("禁用用户请求: userId={}", userId);
        userService.disableUser(userId);
        return Result.success("用户已禁用");
    }

    @PutMapping("/{userId}/enable")
    @Operation(summary = "启用用户", description = "启用指定用户（管理员权限）")
    public Result<String> enableUser(
            @PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("启用用户请求: userId={}", userId);
        userService.enableUser(userId);
        return Result.success("用户已启用");
    }

    @PostMapping("/batch")
    @Operation(summary = "批量获取用户信息", description = "根据用户ID列表批量获取用户信息")
    public Result<List<UserDTO>> getUsersByIds(
            @RequestBody @Parameter(description = "用户ID列表") List<Long> userIds) {
        log.debug("批量获取用户信息请求: userIds={}", userIds);
        List<UserDTO> userDTOs = userService.getUsersByIds(userIds);
        return Result.success(userDTOs);
    }

    @GetMapping("/check/username")
    @Operation(summary = "检查用户名是否可用", description = "检查用户名是否已被注册")
    public Result<Boolean> checkUsernameAvailable(
            @RequestParam @NotBlank String username) {
        log.debug("检查用户名是否可用: username={}", username);
        boolean available = userService.checkUsernameAvailable(username);
        return Result.success(available);
    }

    @GetMapping("/check/email")
    @Operation(summary = "检查邮箱是否可用", description = "检查邮箱是否已被注册")
    public Result<Boolean> checkEmailAvailable(
            @RequestParam @NotBlank String email) {
        log.debug("检查邮箱是否可用: email={}", email);
        boolean available = userService.checkEmailAvailable(email);
        return Result.success(available);
    }

    @GetMapping("/check/phone")
    @Operation(summary = "检查手机号是否可用", description = "检查手机号是否已被注册")
    public Result<Boolean> checkPhoneAvailable(
            @RequestParam @NotBlank String phone) {
        log.debug("检查手机号是否可用: phone={}", phone);
        boolean available = userService.checkPhoneAvailable(phone);
        return Result.success(available);
    }

    @GetMapping("/check/student-id")
    @Operation(summary = "检查学号是否可用", description = "检查学号是否已被注册")
    public Result<Boolean> checkStudentIdAvailable(
            @RequestParam @NotBlank String studentId) {
        log.debug("检查学号是否可用: studentId={}", studentId);
        boolean available = userService.checkStudentIdAvailable(studentId);
        return Result.success(available);
    }

}