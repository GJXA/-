package com.xushu.campus.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.user.dto.CreateUserAddressRequest;
import com.xushu.campus.user.dto.UpdateUserAddressRequest;
import com.xushu.campus.user.dto.UserAddressDTO;
import com.xushu.campus.user.entity.UserAddress;
import com.xushu.campus.user.mapper.UserAddressMapper;
import com.xushu.campus.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户地址服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressMapper userAddressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressDTO createAddress(Long userId, CreateUserAddressRequest request) {
        log.info("创建用户地址: userId={}", userId);

        // 验证请求数据
        validateAddressRequest(request);

        // 创建地址实体
        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(request, address);
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        address.setDeleted(0);

        // 如果设置为默认地址，需要先取消其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            userAddressMapper.clearDefaultByUserId(userId);
        } else {
            // 如果用户还没有地址，则自动设置为默认地址
            Long count = userAddressMapper.selectCount(new LambdaQueryWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId)
                    .eq(UserAddress::getDeleted, 0));
            if (count == 0) {
                address.setIsDefault(1);
            } else {
                address.setIsDefault(0);
            }
        }

        userAddressMapper.insert(address);
        log.info("地址创建成功: addressId={}", address.getId());

        return convertToDTO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressDTO updateAddress(Long userId, Long addressId, UpdateUserAddressRequest request) {
        log.info("更新用户地址: userId={}, addressId={}", userId, addressId);

        // 验证请求数据
        validateAddressRequest(request);

        // 检查地址是否存在且属于该用户
        UserAddress address = getAddressEntity(userId, addressId);

        // 更新地址信息
        BeanUtils.copyProperties(request, address, "id", "userId", "createTime", "deleted");
        address.setUpdateTime(LocalDateTime.now());

        // 处理默认地址设置
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            if (address.getIsDefault() != 1) {
                userAddressMapper.clearDefaultByUserId(userId);
                address.setIsDefault(1);
            }
        } else if (request.getIsDefault() != null && request.getIsDefault() == 0) {
            // 如果取消默认地址，需要确保用户至少还有一个默认地址
            if (address.getIsDefault() == 1) {
                throw BusinessException.paramError("必须至少保留一个默认地址");
            }
        }

        userAddressMapper.updateById(address);
        log.info("地址更新成功: addressId={}", addressId);

        return convertToDTO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long userId, Long addressId) {
        log.info("删除用户地址: userId={}, addressId={}", userId, addressId);

        UserAddress address = getAddressEntity(userId, addressId);

        // 如果是默认地址，不能删除
        if (address.getIsDefault() == 1) {
            throw BusinessException.paramError("不能删除默认地址，请先设置其他地址为默认");
        }

        // 逻辑删除
        address.setDeleted(1);
        address.setUpdateTime(LocalDateTime.now());
        userAddressMapper.updateById(address);

        log.info("地址已逻辑删除: addressId={}", addressId);
    }

    @Override
    public UserAddressDTO getAddressById(Long userId, Long addressId) {
        log.debug("获取地址详情: userId={}, addressId={}", userId, addressId);

        UserAddress address = getAddressEntity(userId, addressId);
        return convertToDTO(address);
    }

    @Override
    public List<UserAddressDTO> getUserAddresses(Long userId) {
        log.debug("获取用户地址列表: userId={}", userId);

        List<UserAddress> addresses = userAddressMapper.selectActiveByUserId(userId);
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        log.info("设置为默认地址: userId={}, addressId={}", userId, addressId);

        UserAddress address = getAddressEntity(userId, addressId);

        // 如果已经是默认地址，直接返回
        if (address.getIsDefault() == 1) {
            return;
        }

        // 取消其他默认地址
        userAddressMapper.clearDefaultByUserId(userId);

        // 设置新的默认地址
        address.setIsDefault(1);
        address.setUpdateTime(LocalDateTime.now());
        userAddressMapper.updateById(address);

        log.info("默认地址设置成功: addressId={}", addressId);
    }

    /**
     * 获取地址实体（包含权限检查）
     */
    private UserAddress getAddressEntity(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || address.getDeleted() == 1) {
            throw BusinessException.notFound("地址不存在");
        }

        // 检查地址是否属于该用户
        if (!address.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权操作该地址");
        }

        return address;
    }

    /**
     * 验证地址请求数据
     */
    private void validateAddressRequest(CreateUserAddressRequest request) {
        // 可以添加更复杂的验证逻辑
        if (request.getReceiverPhone() != null && !isValidPhone(request.getReceiverPhone())) {
            throw BusinessException.paramError("手机号格式不正确");
        }

        if (request.getIsDefault() != null && (request.getIsDefault() < 0 || request.getIsDefault() > 1)) {
            throw BusinessException.paramError("isDefault参数值无效");
        }
    }

    /**
     * 验证地址请求数据
     */
    private void validateAddressRequest(UpdateUserAddressRequest request) {
        // 可以添加更复杂的验证逻辑
        if (request.getReceiverPhone() != null && !isValidPhone(request.getReceiverPhone())) {
            throw BusinessException.paramError("手机号格式不正确");
        }

        if (request.getIsDefault() != null && (request.getIsDefault() < 0 || request.getIsDefault() > 1)) {
            throw BusinessException.paramError("isDefault参数值无效");
        }
    }

    /**
     * 简单手机号验证（实际项目中应使用更复杂的验证）
     */
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 将地址实体转换为DTO
     */
    private UserAddressDTO convertToDTO(UserAddress address) {
        UserAddressDTO dto = new UserAddressDTO();
        BeanUtils.copyProperties(address, dto);
        return dto;
    }

}