package com.bookmall.address.service;

import com.bookmall.address.dto.AddressCreateDTO;
import com.bookmall.address.dto.AddressDTO;
import com.bookmall.address.dto.AddressUpdateDTO;
import com.bookmall.address.entity.Address;

import java.util.List;

/**
 * 收货地址服务接口
 *
 * @author BookMall Team
 */
public interface AddressService {

    // ==================== 查询 ====================

    /**
     * 获取用户的所有地址
     */
    List<AddressDTO> getAddressList(Long userId);

    /**
     * 获取用户的所有地址（默认地址排最前）
     */
    List<AddressDTO> getAddressListWithDefaultFirst(Long userId);

    /**
     * 获取用户的默认地址
     */
    AddressDTO getDefaultAddress(Long userId);

    /**
     * 获取地址详情
     */
    AddressDTO getAddressById(Long userId, Long addressId);

    /**
     * 获取地址实体
     */
    Address getAddressEntity(Long userId, Long addressId);

    // ==================== 管理 ====================

    /**
     * 创建地址
     */
    AddressDTO createAddress(Long userId, AddressCreateDTO request);

    /**
     * 更新地址
     */
    AddressDTO updateAddress(Long userId, Long addressId, AddressUpdateDTO request);

    /**
     * 删除地址
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);

    /**
     * 批量删除用户地址（用户注销时使用）
     */
    void batchDeleteByUserIds(List<Long> userIds);

    // ==================== 验证 ====================

    /**
     * 检查地址是否存在且属于该用户
     */
    boolean existsByUserIdAndAddressId(Long userId, Long addressId);

    /**
     * 检查用户是否有地址
     */
    boolean hasAddress(Long userId);

    /**
     * 获取用户地址数量
     */
    Integer getAddressCount(Long userId);
}