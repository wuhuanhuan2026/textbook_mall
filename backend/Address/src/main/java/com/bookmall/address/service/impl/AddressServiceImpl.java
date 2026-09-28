package com.bookmall.address.service.impl;

import com.bookmall.address.dto.AddressCreateDTO;
import com.bookmall.address.dto.AddressDTO;
import com.bookmall.address.dto.AddressUpdateDTO;
import com.bookmall.address.entity.Address;
import com.bookmall.address.mapper.AddressMapper;
import com.bookmall.address.service.AddressService;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收货地址服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    // 每个用户最大地址数量
    private static final int MAX_ADDRESS_COUNT = 10;

    // ==================== 查询 ====================

    @Override
    public List<AddressDTO> getAddressList(Long userId) {
        List<Address> addresses = addressMapper.selectByUserId(userId);
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddressDTO> getAddressListWithDefaultFirst(Long userId) {
        List<Address> addresses = addressMapper.selectByUserIdWithDefaultFirst(userId);
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getDefaultAddress(Long userId) {
        Address address = addressMapper.selectDefaultByUserId(userId);
        if (address == null) {
            return null;
        }
        return convertToDTO(address);
    }

    @Override
    public AddressDTO getAddressById(Long userId, Long addressId) {
        Address address = getAddressEntity(userId, addressId);
        return convertToDTO(address);
    }

    @Override
    public Address getAddressEntity(Long userId, Long addressId) {
        Address address = addressMapper.selectByUserIdAndAddressId(userId, addressId);
        if (address == null) {
            throw new BusinessException(ExceptionEnum.ADDRESS_NOT_EXIST);
        }
        return address;
    }

    // ==================== 管理 ====================

    @Override
    @Transactional
    public AddressDTO createAddress(Long userId, AddressCreateDTO request) {
        // 检查地址数量是否超限
        int count = addressMapper.countAddressesByUserId(userId);
        if (count >= MAX_ADDRESS_COUNT) {
            throw new BusinessException("地址数量已达上限（" + MAX_ADDRESS_COUNT + "个）");
        }

        // 如果设置为默认地址，先取消所有默认
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
        }
        // 如果是第一个地址，自动设为默认
        if (count == 0) {
            request.setIsDefault(1);
        }

        // 创建地址
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);

        addressMapper.insert(address);

        log.info("创建地址成功: addressId={}, userId={}", address.getAddressId(), userId);

        return convertToDTO(address);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long userId, Long addressId, AddressUpdateDTO request) {
        Address address = getAddressEntity(userId, addressId);

        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
        }

        // 更新
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setIsDefault(request.getIsDefault());

        addressMapper.updateById(address);

        log.info("更新地址成功: addressId={}, userId={}", addressId, userId);

        return convertToDTO(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressEntity(userId, addressId);

        // 如果删除的是默认地址，需要将其他地址设为默认
        if (address.getIsDefault() == 1) {
            // 查找用户的其他地址
            List<Address> otherAddresses = addressMapper.selectByUserId(userId);
            otherAddresses.removeIf(a -> a.getAddressId().equals(addressId));
            if (!otherAddresses.isEmpty()) {
                // 将第一个设为默认
                Address newDefault = otherAddresses.get(0);
                newDefault.setIsDefault(1);
                addressMapper.updateById(newDefault);
            }
        }

        addressMapper.deleteById(addressId);

        log.info("删除地址成功: addressId={}, userId={}", addressId, userId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 检查地址是否存在且属于该用户
        Address address = getAddressEntity(userId, addressId);

        // 如果已经是默认地址，直接返回
        if (address.getIsDefault() == 1) {
            return;
        }

        // 取消所有默认地址
        addressMapper.clearDefaultByUserId(userId);

        // 设置新默认地址
        addressMapper.setDefault(addressId, userId);

        log.info("设置默认地址成功: addressId={}, userId={}", addressId, userId);
    }

    @Override
    @Transactional
    public void batchDeleteByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        addressMapper.batchDeleteByUserIds(userIds);
        log.info("批量删除用户地址成功: userIds={}", userIds);
    }

    // ==================== 验证 ====================

    @Override
    public boolean existsByUserIdAndAddressId(Long userId, Long addressId) {
        return addressMapper.countByAddressIdAndUserId(addressId, userId) > 0;
    }

    @Override
    public boolean hasAddress(Long userId) {
        return addressMapper.countByUserId(userId) > 0;
    }

    @Override
    public Integer getAddressCount(Long userId) {
        return addressMapper.countByUserId(userId);
    }

    // ==================== 私有方法 ====================

    /**
     * 实体转DTO
     */
    private AddressDTO convertToDTO(Address address) {
        if (address == null) {
            return null;
        }
        // 构建完整地址
        String fullAddress = buildFullAddress(address);

        return AddressDTO.builder()
                .addressId(address.getAddressId())
                .userId(address.getUserId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .isDefault(address.getIsDefault())
                .fullAddress(fullAddress)
                .createTime(address.getCreateTime())
                .build();
    }

    /**
     * 构建完整地址
     */
    private String buildFullAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(address.getProvince())) {
            sb.append(address.getProvince());
        }
        if (StringUtils.hasText(address.getCity())) {
            sb.append(address.getCity());
        }
        if (StringUtils.hasText(address.getDistrict())) {
            sb.append(address.getDistrict());
        }
        if (StringUtils.hasText(address.getDetailAddress())) {
            sb.append(address.getDetailAddress());
        }
        return sb.toString();
    }
}