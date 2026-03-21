package com.xushu.campus.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.common.util.JwtUtil;
import com.xushu.campus.user.dto.*;
import com.xushu.campus.user.entity.User;
import com.xushu.campus.user.entity.UserRole;
import com.xushu.campus.user.mapper.UserMapper;
import com.xushu.campus.user.mapper.UserRoleMapper;
import com.xushu.campus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 默认用户角色
     */
    private static final String DEFAULT_ROLE = "STUDENT";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(RegisterRequest request) {
        log.info("用户注册: username={}", request.getUsername());

        // 1. 校验数据
        validateRegisterRequest(request);

        // 2. 创建用户实体
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 3. 保存用户
        userMapper.insert(user);

        // 4. 分配默认角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleCode(DEFAULT_ROLE);
        userRole.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(userRole);

        // 5. 返回用户信息
        return convertToUserDTO(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());

        // 1. 查找用户
        User user = findUserByIdentifier(request.getUsername());
        if (user == null) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw BusinessException.forbidden("账号已被禁用");
        }

        // 4. 获取用户角色
        List<String> roles = getUserRoles(user.getId());

        // 5. 生成令牌
        String accessToken = JwtUtil.generateAccessToken(user.getId(), user.getUsername(),
                String.join(",", roles));
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());

        // 6. 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId());

        // 7. 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(24 * 60 * 60L); // 24小时
        response.setUser(convertToUserDTO(user));

        return response;
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        log.info("刷新令牌");

        if (!JwtUtil.validateToken(refreshToken)) {
            throw BusinessException.unauthorized("刷新令牌无效");
        }

        Long userId = JwtUtil.getUserIdFromToken(refreshToken);
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.unauthorized("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw BusinessException.forbidden("账号已被禁用");
        }

        // 获取用户角色
        List<String> roles = getUserRoles(userId);

        // 生成新的访问令牌
        String newAccessToken = JwtUtil.generateAccessToken(user.getId(), user.getUsername(),
                String.join(",", roles));
        String newRefreshToken = JwtUtil.generateRefreshToken(user.getId());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(24 * 60 * 60L);
        response.setUser(convertToUserDTO(user));

        return response;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("获取用户信息: userId={}", userId);

        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.notFound("用户不存在");
        }

        return convertToUserDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        log.debug("根据用户名获取用户信息: username={}", username);

        User user = userMapper.selectByUsername(username);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.notFound("用户不存在");
        }

        return convertToUserDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
        log.info("更新用户信息: userId={}", userId);

        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.notFound("用户不存在");
        }

        // 更新可修改的字段
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (!checkEmailAvailable(request.getEmail())) {
                throw BusinessException.paramError("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            if (!checkPhoneAvailable(request.getPhone())) {
                throw BusinessException.paramError("手机号已被使用");
            }
            user.setPhone(request.getPhone());
        }

        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }

        if (StringUtils.hasText(request.getStudentId()) && !request.getStudentId().equals(user.getStudentId())) {
            if (!checkStudentIdAvailable(request.getStudentId())) {
                throw BusinessException.paramError("学号已被使用");
            }
            user.setStudentId(request.getStudentId());
        }

        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }

        if (StringUtils.hasText(request.getSchool())) {
            user.setSchool(request.getSchool());
        }

        if (StringUtils.hasText(request.getMajor())) {
            user.setMajor(request.getMajor());
        }

        if (StringUtils.hasText(request.getGrade())) {
            user.setGrade(request.getGrade());
        }

        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress());
        }

        if (StringUtils.hasText(request.getSignature())) {
            user.setSignature(request.getSignature());
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        return convertToUserDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: userId={}", userId);

        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.notFound("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw BusinessException.paramError("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String username, String email) {
        log.info("重置密码: username={}, email={}", username, email);

        User user = findUserByIdentifier(username);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.notFound("用户不存在");
        }

        // 验证邮箱
        if (!user.getEmail().equals(email)) {
            throw BusinessException.paramError("邮箱不匹配");
        }

        // 生成随机密码（实际项目中应该发送重置邮件）
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // TODO: 发送密码重置邮件
        log.info("密码重置成功，新密码: {}", newPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableUser(Long userId) {
        log.info("禁用用户: userId={}", userId);

        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.notFound("用户不存在");
        }

        user.setStatus(0);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableUser(Long userId) {
        log.info("启用用户: userId={}", userId);

        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw BusinessException.notFound("用户不存在");
        }

        user.setStatus(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public List<UserDTO> getUsersByIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }

        List<User> users = userMapper.selectBatchIds(userIds);
        return users.stream()
                .filter(user -> user.getDeleted() == 0)
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkUsernameAvailable(String username) {
        User user = userMapper.selectByUsername(username);
        return user == null || user.getDeleted() == 1;
    }

    @Override
    public boolean checkEmailAvailable(String email) {
        User user = userMapper.selectByEmail(email);
        return user == null || user.getDeleted() == 1;
    }

    @Override
    public boolean checkPhoneAvailable(String phone) {
        User user = userMapper.selectByPhone(phone);
        return user == null || user.getDeleted() == 1;
    }

    @Override
    public boolean checkStudentIdAvailable(String studentId) {
        User user = userMapper.selectByStudentId(studentId);
        return user == null || user.getDeleted() == 1;
    }

    /**
     * 校验注册请求
     */
    private void validateRegisterRequest(RegisterRequest request) {
        // 检查用户名
        if (!checkUsernameAvailable(request.getUsername())) {
            throw BusinessException.paramError("用户名已被使用");
        }

        // 检查邮箱
        if (StringUtils.hasText(request.getEmail()) && !checkEmailAvailable(request.getEmail())) {
            throw BusinessException.paramError("邮箱已被使用");
        }

        // 检查手机号
        if (StringUtils.hasText(request.getPhone()) && !checkPhoneAvailable(request.getPhone())) {
            throw BusinessException.paramError("手机号已被使用");
        }

        // 检查学号
        if (StringUtils.hasText(request.getStudentId()) && !checkStudentIdAvailable(request.getStudentId())) {
            throw BusinessException.paramError("学号已被使用");
        }
    }

    /**
     * 根据标识符查找用户（用户名/邮箱/手机号）
     */
    private User findUserByIdentifier(String identifier) {
        User user = userMapper.selectByUsername(identifier);
        if (user != null && user.getDeleted() == 0) {
            return user;
        }

        user = userMapper.selectByEmail(identifier);
        if (user != null && user.getDeleted() == 0) {
            return user;
        }

        user = userMapper.selectByPhone(identifier);
        if (user != null && user.getDeleted() == 0) {
            return user;
        }

        return null;
    }

    /**
     * 获取用户角色列表
     */
    private List<String> getUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectByUserId(userId);
        return userRoles.stream()
                .map(UserRole::getRoleCode)
                .collect(Collectors.toList());
    }

    /**
     * 将User实体转换为UserDTO
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        // 获取用户角色
        List<String> roles = getUserRoles(user.getId());
        userDTO.setRoles(roles);

        return userDTO;
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        // 生成8位随机密码（包含大小写字母和数字）
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

}