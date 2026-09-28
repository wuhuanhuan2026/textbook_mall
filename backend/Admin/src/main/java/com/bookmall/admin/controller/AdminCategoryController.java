package com.bookmall.admin.controller;

import com.bookmall.admin.feign.CategoryFeignClient;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 分类管理控制器
 * 通过Feign调用Category服务
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理后台-分类管理", description = "管理员分类管理接口")
public class AdminCategoryController {

    private final CategoryFeignClient categoryFeignClient;

    // ==================== 分类管理 ====================

    /**
     * 获取所有分类列表（含教材数量）
     */
    @GetMapping
    @Operation(summary = "获取所有分类列表")
    public Result<List<Map<String, Object>>> getCategories() {
        log.info("管理员获取所有分类列表");
        return categoryFeignClient.getCategories();
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "获取分类详情")
    public Result<Map<String, Object>> getCategoryById(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId) {
        log.info("管理员获取分类详情: categoryId={}", categoryId);
        return categoryFeignClient.getCategoryById(categoryId);
    }

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类")
    public Result<Map<String, Object>> createCategory(@RequestBody Map<String, Object> request) {
        log.info("管理员创建分类: request={}", request);
        return categoryFeignClient.createCategory(request);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    @Operation(summary = "更新分类")
    public Result<Map<String, Object>> updateCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId,
            @RequestBody Map<String, Object> request) {
        log.info("管理员更新分类: categoryId={}, request={}", categoryId, request);
        return categoryFeignClient.updateCategory(categoryId, request);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId) {
        log.info("管理员删除分类: categoryId={}", categoryId);
        return categoryFeignClient.deleteCategory(categoryId);
    }

    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除分类")
    public Result<Void> batchDeleteCategories(@RequestBody List<Long> categoryIds) {
        log.info("管理员批量删除分类: categoryIds={}", categoryIds);
        return categoryFeignClient.batchDeleteCategories(categoryIds);
    }

    /**
     * 更新分类排序
     */
    @PutMapping("/{categoryId}/sort")
    @Operation(summary = "更新分类排序")
    public Result<Void> updateSortOrder(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId,
            @RequestParam @NotNull Integer sortOrder) {
        log.info("管理员更新分类排序: categoryId={}, sortOrder={}", categoryId, sortOrder);
        return categoryFeignClient.updateSortOrder(categoryId, sortOrder);
    }

    /**
     * 获取分类下的教材数量
     */
    @GetMapping("/{categoryId}/book-count")
    @Operation(summary = "获取分类下的教材数量")
    public Result<Integer> getBookCount(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId) {
        return categoryFeignClient.getBookCount(categoryId);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量更新分类排序
     */
    @PutMapping("/batch/sort")
    @Operation(summary = "批量更新分类排序")
    public Result<Void> batchUpdateSort(@RequestBody List<Map<String, Object>> categories) {
        log.info("管理员批量更新分类排序: categories={}", categories);
        return categoryFeignClient.batchUpdateSort(categories);
    }

    /**
     * 检查分类是否存在
     */
    @GetMapping("/exists/{categoryId}")
    @Operation(summary = "检查分类是否存在", description = "内部调用")
    public Result<Boolean> existsById(
            @PathVariable @NotNull Long categoryId) {
        return categoryFeignClient.existsById(categoryId);
    }
}