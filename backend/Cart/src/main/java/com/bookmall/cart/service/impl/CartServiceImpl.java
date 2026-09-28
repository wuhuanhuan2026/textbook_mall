package com.bookmall.cart.service.impl;

import com.bookmall.book.dto.BookDTO;
import com.bookmall.cart.dto.CartAddDTO;
import com.bookmall.cart.dto.CartDTO;
import com.bookmall.cart.dto.CartUpdateDTO;
import com.bookmall.cart.entity.Cart;
import com.bookmall.cart.feign.BookFeignClient;
import com.bookmall.cart.feign.UserFeignClient;
import com.bookmall.cart.mapper.CartMapper;
import com.bookmall.cart.service.CartService;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.Result;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final BookFeignClient bookFeignClient;
    private final UserFeignClient userFeignClient;
    private final RedisUtil redisUtil;

    // Redis缓存Key前缀
    private static final String REDIS_CART_KEY = "cart:";
    private static final String REDIS_CART_COUNT_KEY = "cart:count:";

    // ==================== 查询 ====================

    @Override
    public List<CartDTO> getCartList(Long userId) {
        // 从数据库查询
        List<Cart> carts = cartMapper.selectCartListByUserId(userId);
        return carts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getCartCount(Long userId) {
        // 先从Redis缓存获取
        String cacheKey = REDIS_CART_COUNT_KEY + userId;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            try {
                return (Integer) cached;
            } catch (Exception e) {
                log.warn("Redis缓存解析失败，从数据库查询");
            }
        }

        Integer count = cartMapper.sumQuantityByUserId(userId);
        if (count == null) {
            count = 0;
        }

        // 缓存到Redis（30分钟过期）
        redisUtil.set(cacheKey, count, 30, TimeUnit.MINUTES);

        return count;
    }

    @Override
    public BigDecimal getCartTotalAmount(Long userId) {
        BigDecimal total = cartMapper.sumTotalAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public CartDTO getCartItem(Long userId, Long cartId) {
        Cart cart = getCartEntity(userId, cartId);
        return convertToDTO(cart);
    }

    @Override
    public Cart getCartEntity(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new BusinessException(ExceptionEnum.CART_ITEM_NOT_EXIST);
        }
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不属于该用户");
        }
        return cart;
    }

    // ==================== 操作 ====================

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, CartAddDTO request) {
        log.info("添加到购物车: userId={}, bookId={}, quantity={}", userId, request.getBookId(), request.getQuantity());

        // 1. 验证用户是否存在
        try {
            Result<Boolean> userResult = userFeignClient.checkUserExists(userId);
            if (userResult == null || !userResult.isSuccess() || !userResult.getData()) {
                throw new BusinessException(ExceptionEnum.USER_NOT_EXIST);
            }
        } catch (Exception e) {
            log.error("调用用户服务失败: {}", e.getMessage());
            throw new BusinessException("用户服务不可用，请稍后再试");
        }

        // 2. ✅ 获取图书详情（替代 existsById + isOnSale + getStock 三次调用）
        BookDTO book;
        try {
            Result<com.bookmall.book.dto.BookDTO> bookResult = bookFeignClient.getBookDetail(request.getBookId());
            if (bookResult == null || !bookResult.isSuccess() || bookResult.getData() == null) {
                throw new BusinessException(ExceptionEnum.BOOK_NOT_EXIST);
            }
            book = bookResult.getData();
            log.info("获取图书信息成功: bookId={}, bookName={}, price={}, stock={}, status={}",
                    book.getBookId(), book.getBookName(), book.getPrice(), book.getStock(), book.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用图书服务失败: bookId={}, error={}", request.getBookId(), e.getMessage());
            throw new BusinessException("图书服务不可用，请稍后再试");
        }

        // 3. 检查图书是否上架
        if (book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException(ExceptionEnum.BOOK_OFF_SALE);
        }

        // 4. 验证库存
        if (book.getStock() == null || book.getStock() < request.getQuantity()) {
            throw new BusinessException(ExceptionEnum.STOCK_INSUFFICIENT);
        }

        // 5. 检查是否已存在该商品
        Cart existCart = cartMapper.selectByUserIdAndBookId(userId, request.getBookId());

        if (existCart != null) {
            // 已存在，增加数量
            int newQuantity = existCart.getQuantity() + request.getQuantity();
            if (newQuantity > book.getStock()) {
                throw new BusinessException(ExceptionEnum.STOCK_INSUFFICIENT);
            }
            existCart.setQuantity(newQuantity);
            cartMapper.updateById(existCart);
            log.info("购物车商品数量增加: cartId={}, quantity={}", existCart.getCartId(), newQuantity);
            clearCache(userId);
            return convertToDTO(existCart);
        } else {
            // 新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setBookId(request.getBookId());
            cart.setQuantity(request.getQuantity());
            cartMapper.insert(cart);
            log.info("添加购物车成功: userId={}, bookId={}, quantity={}",
                    userId, request.getBookId(), request.getQuantity());
            clearCache(userId);
            return convertToDTO(cart);
        }
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, Long cartId, CartUpdateDTO request) {
        Cart cart = getCartEntity(userId, cartId);

        // 验证库存（使用 Feign 调用）
        try {
            Result<Integer> stockResult = bookFeignClient.getStock(cart.getBookId());
            Integer stock = stockResult != null && stockResult.isSuccess() ? stockResult.getData() : 0;
            if (stock < request.getQuantity()) {
                throw new BusinessException(ExceptionEnum.STOCK_INSUFFICIENT);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用图书服务获取库存失败: bookId={}", cart.getBookId());
            throw new BusinessException("图书服务不可用，请稍后再试");
        }

        cart.setQuantity(request.getQuantity());
        cartMapper.updateById(cart);

        clearCache(userId);

        log.info("更新购物车成功: cartId={}, quantity={}", cartId, request.getQuantity());

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public void deleteCartItem(Long userId, Long cartId) {
        Cart cart = getCartEntity(userId, cartId);
        cartMapper.deleteById(cartId);

        clearCache(userId);

        log.info("删除购物车项成功: cartId={}, userId={}", cartId, userId);
    }

    @Override
    @Transactional
    public void batchDeleteCartItems(Long userId, List<Long> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return;
        }

        // 验证所有购物车项都属于该用户
        for (Long cartId : cartIds) {
            getCartEntity(userId, cartId);
        }

        cartMapper.batchDelete(cartIds);

        clearCache(userId);

        log.info("批量删除购物车项成功: userId={}, cartIds={}", userId, cartIds);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.clearByUserId(userId);

        clearCache(userId);

        log.info("清空购物车成功: userId={}", userId);
    }

    @Override
    @Transactional
    public void batchDeleteByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        cartMapper.batchDeleteByUserIds(userIds);

        // 清除缓存
        for (Long userId : userIds) {
            clearCache(userId);
        }

        log.info("批量删除用户购物车成功: userIds={}", userIds);
    }

    // ==================== 验证 ====================

    @Override
    public boolean belongsToUser(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        return cart != null && cart.getUserId().equals(userId);
    }

    @Override
    public boolean isCartEmpty(Long userId) {
        Integer count = cartMapper.countByUserId(userId);
        return count == null || count == 0;
    }

    // ==================== 私有方法 ====================

    /**
     * 实体转DTO
     */
    private CartDTO convertToDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        BigDecimal price = cart.getPrice() != null ? cart.getPrice() : BigDecimal.ZERO;
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return CartDTO.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .bookId(cart.getBookId())
                .bookName(cart.getBookName())
                .bookCover(cart.getBookCover())
                .author(cart.getAuthor())
                .price(price)
                .quantity(cart.getQuantity())
                .subtotal(subtotal)
                .stock(cart.getStock())
                .onSale(cart.getOnSale() != null && cart.getOnSale())
                .createTime(cart.getCreateTime())
                .updateTime(cart.getUpdateTime())
                .build();
    }

    /**
     * 清除用户购物车缓存
     */
    private void clearCache(Long userId) {
        redisUtil.delete(REDIS_CART_COUNT_KEY + userId);
        redisUtil.delete(REDIS_CART_KEY + userId);
    }
}