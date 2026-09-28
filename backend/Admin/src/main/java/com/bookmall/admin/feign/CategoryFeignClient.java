package com.bookmall.admin.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类服务 Feign 客户端
 *
 * 路径说明：
 * - Category 服务的 CategoryController 映射为 @RequestMapping("/categories")
 *
 * @author BookMall Team
 */
@FeignClient(name = "category-service", path = "/categories")
public interface CategoryFeignClient {

    @GetMapping
    Result<List<Map<String, Object>>> getCategories();

    @GetMapping("/{categoryId}")
    Result<Map<String, Object>> getCategoryById(@PathVariable("categoryId") Long categoryId);

    @PostMapping
    Result<Map<String, Object>> createCategory(@RequestBody Map<String, Object> request);

    @PutMapping("/{categoryId}")
    Result<Map<String, Object>> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody Map<String, Object> request);

    @DeleteMapping("/{categoryId}")
    Result<Void> deleteCategory(@PathVariable("categoryId") Long categoryId);

    @DeleteMapping("/batch")
    Result<Void> batchDeleteCategories(@RequestBody List<Long> categoryIds);

    @PutMapping("/{categoryId}/sort")
    Result<Void> updateSortOrder(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("sortOrder") Integer sortOrder);

    @GetMapping("/{categoryId}/book-count")
    Result<Integer> getBookCount(@PathVariable("categoryId") Long categoryId);

    @PutMapping("/batch/sort")
    Result<Void> batchUpdateSort(@RequestBody List<Map<String, Object>> categories);

    @GetMapping("/exists/{categoryId}")
    Result<Boolean> existsById(@PathVariable("categoryId") Long categoryId);
}