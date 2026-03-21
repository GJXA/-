package com.xushu.campus.user.service;

import com.xushu.campus.user.dto.CreateUserAddressRequest;
import com.xushu.campus.user.dto.UpdateUserAddressRequest;
import com.xushu.campus.user.dto.UserAddressDTO;

import java.util.List;

/**
 * 用户地址服务接口
 */
public interface UserAddressService {

    /**
     * 创建用户地址
     */
    UserAddressDTO createAddress(Long userId, CreateUserAddressRequest request);

    /**
     * 更新用户地址
     */
    UserAddressDTO updateAddress(Long userId, Long addressId, UpdateUserAddressRequest request);

    /**
     * 删除用户地址（逻辑删除）
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 根据ID获取地址详情
     */
    UserAddressDTO getAddressById(Long userId, Long addressId);

    /**
     * 获取用户的所有地址列表
     */
    List<UserAddressDTO> getUserAddresses(Long userId);

    /**
     * 设置为默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);

}