package com.bookmall.order.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 地址服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "address-service", path = "/address")  // ✅ 修改为 /address
public interface AddressFeignClient {

    /**
     * 获取地址详情
     */
    @GetMapping("/{addressId}")
    Result<Object> getAddressById(@PathVariable("addressId") Long addressId,
                                  @RequestParam("userId") Long userId);

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    Result<Object> getDefaultAddress(@RequestParam("userId") Long userId);

    /**
     * 检查地址是否存在且属于该用户
     */
    @GetMapping("/exists")
    Result<Boolean> existsByUserIdAndAddressId(@RequestParam("userId") Long userId,
                                               @RequestParam("addressId") Long addressId);
}