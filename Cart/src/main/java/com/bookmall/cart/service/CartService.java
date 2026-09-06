package com.bookmall.cart.service;

import com.bookmall.cart.dto.CartAddDTO;
import com.bookmall.cart.dto.CartDTO;
import com.bookmall.cart.dto.CartUpdateDTO;
import com.bookmall.cart.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车服务接口
 *
 * @author BookMall Team
 */
public interface CartService {

    // ==================== 查询 ====================

    /**
     * 获取用户购物车列表
     */
    List<CartDTO> getCartList(Long userId);

    /**
     * 获取用户购物车商品总数量
     */
    Integer getCartCount(Long userId);

    /**
     * 获取用户购物车总金额
     */
    BigDecimal getCartTotalAmount(Long userId);

    /**
     * 获取购物车项详情
     */
    CartDTO getCartItem(Long userId, Long cartId);

    /**
     * 获取购物车实体
     */
    Cart getCartEntity(Long userId, Long cartId);

    // ==================== 操作 ====================

    /**
     * 添加到购物车
     */
    CartDTO addToCart(Long userId, CartAddDTO request);

    /**
     * 更新购物车数量
     */
    CartDTO updateCartItem(Long userId, Long cartId, CartUpdateDTO request);

    /**
     * 删除购物车项
     */
    void deleteCartItem(Long userId, Long cartId);

    /**
     * 批量删除购物车项
     */
    void batchDeleteCartItems(Long userId, List<Long> cartIds);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);

    /**
     * 批量删除用户购物车（用户注销时使用）
     */
    void batchDeleteByUserIds(List<Long> userIds);

    // ==================== 验证 ====================

    /**
     * 检查购物车项是否属于该用户
     */
    boolean belongsToUser(Long userId, Long cartId);

    /**
     * 检查用户购物车是否为空
     */
    boolean isCartEmpty(Long userId);
}