package com.bookmall.address.controller;

import com.bookmall.address.dto.AddressCreateDTO;
import com.bookmall.address.dto.AddressDTO;
import com.bookmall.address.dto.AddressUpdateDTO;
import com.bookmall.address.service.AddressService;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 收货地址控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@Validated
@Tag(name = "地址接口", description = "收货地址管理")
public class AddressController {

    private final AddressService addressService;

    /**
     * 从请求头获取用户ID
     * Gateway 的 AuthGlobalFilter 会将 userId 放入请求头 X-User-Id
     */
    private Long getUserId(HttpServletRequest request) {
        // 1. 优先从请求头获取
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("解析 userId 失败: {}", userIdHeader);
            }
        }
        // 2. 兼容从请求属性获取
        Object userIdAttr = request.getAttribute(Constants.REQ_ATTR_USER_ID);
        if (userIdAttr instanceof Long) {
            return (Long) userIdAttr;
        }
        throw new RuntimeException("用户未登录");
    }

    /**
     * 获取用户的所有地址
     */
    @GetMapping
    @Operation(summary = "获取地址列表")
    public Result<List<AddressDTO>> getAddressList(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<AddressDTO> addresses = addressService.getAddressListWithDefaultFirst(userId);
        return Result.success(addresses);
    }

    /**
     * 获取地址详情
     * 支持两种方式获取 userId：
     * 1. 从请求头 X-User-Id 获取（前端调用）
     * 2. 从请求参数获取（Feign 内部调用）
     */
    @GetMapping("/{addressId}")
    @Operation(summary = "获取地址详情")
    public Result<AddressDTO> getAddressById(
            HttpServletRequest request,
            @Parameter(description = "地址ID", required = true)
            @PathVariable @NotNull Long addressId,
            @RequestParam(required = false) Long userId) {  // ✅ 添加 userId 参数

        log.info("获取地址详情: addressId={}, userId={}", addressId, userId);

        Long finalUserId = userId;
        if (finalUserId == null) {
            try {
                finalUserId = getUserId(request);
            } catch (Exception e) {
                log.error("获取 userId 失败: {}", e.getMessage());
                return Result.error("用户未登录");
            }
        }

        if (finalUserId == null) {
            return Result.error("用户ID不能为空");
        }

        AddressDTO address = addressService.getAddressById(finalUserId, addressId);
        return Result.success(address);
    }

    /**
     * 获取默认地址
     * 支持两种方式获取 userId：
     * 1. 从请求头 X-User-Id 获取（前端调用）
     * 2. 从请求参数获取（Feign 内部调用）
     */
    @GetMapping("/default")
    @Operation(summary = "获取默认地址")
    public Result<AddressDTO> getDefaultAddress(
            HttpServletRequest request,
            @RequestParam(required = false) Long userId) {  // ✅ 添加 userId 参数

        log.info("获取默认地址: userId={}", userId);

        Long finalUserId = userId;
        if (finalUserId == null) {
            try {
                finalUserId = getUserId(request);
            } catch (Exception e) {
                log.error("获取 userId 失败: {}", e.getMessage());
                return Result.error("用户未登录");
            }
        }

        if (finalUserId == null) {
            return Result.error("用户ID不能为空");
        }

        AddressDTO address = addressService.getDefaultAddress(finalUserId);
        return Result.success(address);
    }

    /**
     * 创建地址
     */
    @PostMapping
    @Operation(summary = "新增地址")
    public Result<AddressDTO> createAddress(
            HttpServletRequest request,
            @Valid @RequestBody AddressCreateDTO addressCreateDTO) {
        Long userId = getUserId(request);
        AddressDTO address = addressService.createAddress(userId, addressCreateDTO);
        return Result.success("添加成功", address);
    }

    /**
     * 更新地址
     */
    @PutMapping("/{addressId}")
    @Operation(summary = "修改地址")
    public Result<AddressDTO> updateAddress(
            HttpServletRequest request,
            @Parameter(description = "地址ID", required = true)
            @PathVariable @NotNull Long addressId,
            @Valid @RequestBody AddressUpdateDTO addressUpdateDTO) {
        Long userId = getUserId(request);
        AddressDTO address = addressService.updateAddress(userId, addressId, addressUpdateDTO);
        return Result.success("修改成功", address);
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    @Operation(summary = "删除地址")
    public Result<Void> deleteAddress(
            HttpServletRequest request,
            @Parameter(description = "地址ID", required = true)
            @PathVariable @NotNull Long addressId) {
        Long userId = getUserId(request);
        addressService.deleteAddress(userId, addressId);
        return Result.success("删除成功");
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{addressId}/default")
    @Operation(summary = "设置默认地址")
    public Result<Void> setDefaultAddress(
            HttpServletRequest request,
            @Parameter(description = "地址ID", required = true)
            @PathVariable @NotNull Long addressId) {
        Long userId = getUserId(request);
        addressService.setDefaultAddress(userId, addressId);
        return Result.success("设置默认地址成功");
    }

    /**
     * 检查用户是否有地址
     */
    @GetMapping("/check")
    @Operation(summary = "检查用户是否有地址", description = "内部调用")
    public Result<Boolean> hasAddress(
            @RequestParam @NotNull Long userId) {
        boolean has = addressService.hasAddress(userId);
        return Result.success(has);
    }

    /**
     * 获取地址数量
     */
    @GetMapping("/count")
    @Operation(summary = "获取地址数量", description = "内部调用")
    public Result<Integer> getAddressCount(
            @RequestParam @NotNull Long userId) {
        Integer count = addressService.getAddressCount(userId);
        return Result.success(count);
    }
}