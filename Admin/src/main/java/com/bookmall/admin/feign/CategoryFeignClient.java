package com.bookmall.admin.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 分类服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "category-service", path = "/api/categories")
public interface CategoryFeignClient {

    // ==================== 分类管理 ====================

    /**
     * 获取所有分类列表（含教材数量）
     */
    @GetMapping
    Result<List<Map<String, Object>>> getCategories();

    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    Result<Map<String, Object>> getCategoryById(@PathVariable("categoryId") Long categoryId);

    /**
     * 创建分类
     */
    @PostMapping
    Result<Map<String, Object>> createCategory(@RequestBody Map<String, Object> request);

    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    Result<Map<String, Object>> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody Map<String, Object> request);

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    Result<Void> deleteCategory(@PathVariable("categoryId") Long categoryId);

    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    Result<Void> batchDeleteCategories(@RequestBody List<Long> categoryIds);

    /**
     * 更新分类排序
     */
    @PutMapping("/{categoryId}/sort")
    Result<Void> updateSortOrder(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("sortOrder") Integer sortOrder);

    /**
     * 获取分类下的教材数量
     */
    @GetMapping("/{categoryId}/book-count")
    Result<Integer> getBookCount(@PathVariable("categoryId") Long categoryId);

    // ==================== 批量操作 ====================

    /**
     * 批量更新分类排序
     */
    @PutMapping("/batch/sort")
    Result<Void> batchUpdateSort(@RequestBody List<Map<String, Object>> categories);

    // ==================== 内部调用 ====================

    /**
     * 检查分类是否存在
     */
    @GetMapping("/exists/{categoryId}")
    Result<Boolean> existsById(@PathVariable("categoryId") Long categoryId);
}