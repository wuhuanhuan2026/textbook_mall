package com.bookmall.cart.controller;

import com.bookmall.cart.dto.CartAddDTO;
import com.bookmall.cart.dto.CartDTO;
import com.bookmall.cart.dto.CartUpdateDTO;
import com.bookmall.cart.service.CartService;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Validated
@Tag(name = "购物车接口", description = "购物车管理")
public class CartController {

    private final CartService cartService;

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
     * 获取购物车列表
     * 支持两种方式获取 userId：
     * 1. 从请求头 X-User-Id 获取（前端调用）
     * 2. 从请求参数获取（Feign 内部调用）
     */
    @GetMapping
    @Operation(summary = "获取购物车列表")
    public Result<List<CartDTO>> getCartList(
            HttpServletRequest request,
            @RequestParam(required = false) Long userId) {
        log.info("获取购物车列表: userId={}", userId);

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

        List<CartDTO> cartList = cartService.getCartList(finalUserId);
        return Result.success(cartList);
    }

    /**
     * 获取购物车商品总数
     * 支持两种方式获取 userId
     */
    @GetMapping("/count")
    @Operation(summary = "获取购物车商品总数")
    public Result<Integer> getCartCount(
            HttpServletRequest request,
            @RequestParam(required = false) Long userId) {
        log.info("获取购物车商品总数: userId={}", userId);

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

        Integer count = cartService.getCartCount(finalUserId);
        return Result.success(count);
    }

    /**
     * 获取购物车总金额
     * 支持两种方式获取 userId
     */
    @GetMapping("/total")
    @Operation(summary = "获取购物车总金额")
    public Result<BigDecimal> getCartTotal(
            HttpServletRequest request,
            @RequestParam(required = false) Long userId) {
        log.info("获取购物车总金额: userId={}", userId);

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

        BigDecimal total = cartService.getCartTotalAmount(finalUserId);
        return Result.success(total);
    }

    /**
     * 添加购物车
     */
    @PostMapping
    @Operation(summary = "添加到购物车")
    public Result<CartDTO> addToCart(
            HttpServletRequest request,
            @Valid @RequestBody CartAddDTO cartAddDTO) {
        Long userId = getUserId(request);
        CartDTO cart = cartService.addToCart(userId, cartAddDTO);
        return Result.success("添加成功", cart);
    }

    /**
     * 更新购物车
     */
    @PutMapping("/{cartId}")
    @Operation(summary = "更新购物车数量")
    public Result<CartDTO> updateCartItem(
            HttpServletRequest request,
            @Parameter(description = "购物车ID", required = true)
            @PathVariable @NotNull Long cartId,
            @Valid @RequestBody CartUpdateDTO cartUpdateDTO) {
        Long userId = getUserId(request);
        CartDTO cart = cartService.updateCartItem(userId, cartId, cartUpdateDTO);
        return Result.success("更新成功", cart);
    }

    /**
     * 删除购物车项
     */
    @DeleteMapping("/{cartId}")
    @Operation(summary = "删除购物车项")
    public Result<Void> deleteCartItem(
            HttpServletRequest request,
            @Parameter(description = "购物车ID", required = true)
            @PathVariable @NotNull Long cartId) {
        Long userId = getUserId(request);
        cartService.deleteCartItem(userId, cartId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除购物车项
     * 支持两种方式获取 userId：
     * 1. 从请求头 X-User-Id 获取（前端调用）
     * 2. 从请求参数获取（Feign 内部调用）
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除购物车项")
    public Result<Void> batchDeleteCartItems(
            HttpServletRequest request,
            @RequestParam(required = false) Long userId,
            @RequestBody List<Long> cartIds) {
        log.info("批量删除购物车项: userId={}, cartIds={}", userId, cartIds);

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

        cartService.batchDeleteCartItems(finalUserId, cartIds);
        return Result.success("批量删除成功");
    }

    /**
     * 清空购物车
     * 支持两种方式获取 userId：
     * 1. 从请求头 X-User-Id 获取（前端调用）
     * 2. 从请求参数获取（Feign 内部调用）
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车")
    public Result<Void> clearCart(
            HttpServletRequest request,
            @RequestParam(required = false) Long userId) {
        log.info("清空购物车: userId={}", userId);

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

        cartService.clearCart(finalUserId);
        return Result.success("清空成功");
    }

    /**
     * 检查购物车是否为空（内部调用）
     */
    @GetMapping("/empty")
    @Operation(summary = "检查购物车是否为空", description = "内部调用")
    public Result<Boolean> isCartEmpty(@RequestParam @NotNull Long userId) {
        boolean empty = cartService.isCartEmpty(userId);
        return Result.success(empty);
    }
}