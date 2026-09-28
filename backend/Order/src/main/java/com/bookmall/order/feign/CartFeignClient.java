package com.bookmall.order.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 购物车服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "cart-service", path = "/cart")  // ✅ 修改为 /cart
public interface CartFeignClient {

    /**
     * 获取用户购物车列表
     */
    @GetMapping
    Result<List<Object>> getCartList(@RequestParam("userId") Long userId);

    /**
     * 批量删除购物车项
     */
    @DeleteMapping("/batch")
    Result<Void> batchDeleteCartItems(@RequestParam("userId") Long userId,
                                      @RequestParam("cartIds") List<Long> cartIds);

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    Result<Void> clearCart(@RequestParam("userId") Long userId);

    /**
     * 检查购物车是否为空
     */
    @GetMapping("/empty")
    Result<Boolean> isCartEmpty(@RequestParam("userId") Long userId);
}