package com.bookmall.review.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 教材服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "book-service", path = "/api/books")
public interface BookFeignClient {

    /**
     * 检查教材是否存在
     */
    @GetMapping("/exists/{bookId}")
    Result<Boolean> existsById(@PathVariable("bookId") Long bookId);

    /**
     * 获取教材详情
     */
    @GetMapping("/{bookId}")
    Result<Object> getBookDetail(@PathVariable("bookId") Long bookId);
}