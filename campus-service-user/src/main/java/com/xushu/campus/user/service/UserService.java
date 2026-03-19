package com.xushu.campus.user.service;

import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.user.dto.*;
import com.xushu.campus.user.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserDTO register(RegisterRequest request) throws BusinessException;

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request) throws BusinessException;

    /**
     * 刷新令牌
     */
    LoginResponse refreshToken(String refreshToken) throws BusinessException;

    /**
     * 根据ID获取用户信息
     */
    UserDTO getUserById(Long userId) throws BusinessException;

    /**
     * 根据用户名获取用户信息
     */
    UserDTO getUserByUsername(String username) throws BusinessException;

    /**
     * 更新用户信息
     */
    UserDTO updateUser(Long userId, UpdateUserRequest request) throws BusinessException;

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword) throws BusinessException;

    /**
     * 重置密码
     */
    void resetPassword(String username, String email) throws BusinessException;

    /**
     * 禁用用户
     */
    void disableUser(Long userId) throws BusinessException;

    /**
     * 启用用户
     */
    void enableUser(Long userId) throws BusinessException;

    /**
     * 批量获取用户信息
     */
    List<UserDTO> getUsersByIds(List<Long> userIds);

    /**
     * 检查用户名是否可用
     */
    boolean checkUsernameAvailable(String username);

    /**
     * 检查邮箱是否可用
     */
    boolean checkEmailAvailable(String email);

    /**
     * 检查手机号是否可用
     */
    boolean checkPhoneAvailable(String phone);

    /**
     * 检查学号是否可用
     */
    boolean checkStudentIdAvailable(String studentId);

}