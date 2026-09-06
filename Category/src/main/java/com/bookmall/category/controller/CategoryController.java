package com.bookmall.category.controller;

import com.bookmall.category.dto.CategoryCreateDTO;
import com.bookmall.category.dto.CategoryDTO;
import com.bookmall.category.dto.CategoryListVO;
import com.bookmall.category.entity.Category;
import com.bookmall.category.service.CategoryService;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 分类控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/categories")  // 修改：去掉 /api，Gateway 的 StripPrefix=1 会移除 /api 前缀
@RequiredArgsConstructor
@Validated
@Tag(name = "分类接口", description = "教材分类管理")
public class CategoryController {

    private final CategoryService categoryService;

    // ==================== 公开接口 ====================

    /**
     * 获取所有分类列表（含教材数量）
     */
    @GetMapping
    @Operation(summary = "获取所有分类", description = "返回所有分类，含教材数量，按排序升序")
    public Result<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "获取分类详情")
    public Result<CategoryDTO> getCategoryById(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId) {
        CategoryDTO category = categoryService.getCategoryById(categoryId);
        return Result.success(category);
    }

    /**
     * 获取分类下的教材数量
     */
    @GetMapping("/{categoryId}/book-count")
    @Operation(summary = "获取分类下的教材数量")
    public Result<Integer> getBookCount(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId) {
        Integer count = categoryService.getBookCountByCategoryId(categoryId);
        return Result.success(count);
    }

    // ==================== 管理接口（需要管理员权限） ====================

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "管理员操作")
    public Result<CategoryDTO> createCategory(
            @Valid @RequestBody CategoryCreateDTO request) {
        CategoryDTO category = categoryService.createCategory(request);
        return Result.success("创建成功", category);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    @Operation(summary = "更新分类", description = "管理员操作")
    public Result<CategoryDTO> updateCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId,
            @Valid @RequestBody CategoryCreateDTO request) {
        CategoryDTO category = categoryService.updateCategory(categoryId, request);
        return Result.success("更新成功", category);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "删除分类", description = "管理员操作")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除分类", description = "管理员操作")
    public Result<Void> batchDeleteCategories(
            @RequestBody List<Long> categoryIds) {
        categoryService.batchDeleteCategories(categoryIds);
        return Result.success("批量删除成功");
    }

    /**
     * 更新分类排序
     */
    @PutMapping("/{categoryId}/sort")
    @Operation(summary = "更新分类排序", description = "管理员操作")
    public Result<Void> updateSortOrder(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId,
            @RequestParam @NotNull Integer sortOrder) {
        categoryService.updateSortOrder(categoryId, sortOrder);
        return Result.success("排序更新成功");
    }

    /**
     * 检查分类是否存在
     */
    @GetMapping("/exists/{categoryId}")
    @Operation(summary = "检查分类是否存在", description = "内部调用")
    public Result<Boolean> existsById(
            @PathVariable @NotNull Long categoryId) {
        boolean exists = categoryService.existsById(categoryId);
        return Result.success(exists);
    }
}