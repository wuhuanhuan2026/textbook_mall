package com.bookmall.category.feign;

import com.bookmall.category.dto.CategoryDTO;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 分类服务 Feign 客户端
 * 供其他微服务调用
 *
 * @author BookMall Team
 */
@FeignClient(name = "category-service", path = "/api/categories")
public interface CategoryFeignClient {

    /**
     * 获取所有分类
     */
    @GetMapping
    Result<List<CategoryDTO>> getCategories();

    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    Result<CategoryDTO> getCategoryById(@PathVariable("categoryId") Long categoryId);

    /**
     * 检查分类是否存在
     */
    @GetMapping("/exists/{categoryId}")
    Result<Boolean> existsById(@PathVariable("categoryId") Long categoryId);

    /**
     * 获取分类下的教材数量
     */
    @GetMapping("/{categoryId}/book-count")
    Result<Integer> getBookCount(@PathVariable("categoryId") Long categoryId);
}