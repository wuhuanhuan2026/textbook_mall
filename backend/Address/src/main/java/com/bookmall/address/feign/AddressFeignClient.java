package com.bookmall.address.feign;

import com.bookmall.address.dto.AddressDTO;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 收货地址服务 Feign 客户端
 * 供其他微服务调用
 *
 * @author BookMall Team
 */
@FeignClient(name = "address-service", path = "/api/address")
public interface AddressFeignClient {

    /**
     * 获取用户的所有地址
     */
    @GetMapping
    Result<List<AddressDTO>> getAddressList(@RequestParam("userId") Long userId);

    /**
     * 获取地址详情
     */
    @GetMapping("/{addressId}")
    Result<AddressDTO> getAddressById(@RequestParam("userId") Long userId,
                                      @PathVariable("addressId") Long addressId);

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    Result<AddressDTO> getDefaultAddress(@RequestParam("userId") Long userId);

    /**
     * 检查用户是否有地址
     */
    @GetMapping("/check")
    Result<Boolean> hasAddress(@RequestParam("userId") Long userId);

    /**
     * 检查地址是否存在且属于该用户
     */
    @GetMapping("/exists")
    Result<Boolean> existsByUserIdAndAddressId(@RequestParam("userId") Long userId,
                                               @RequestParam("addressId") Long addressId);

    /**
     * 获取地址数量
     */
    @GetMapping("/count")
    Result<Integer> getAddressCount(@RequestParam("userId") Long userId);
}