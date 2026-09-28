package com.bookmall.category.service;

import com.bookmall.category.dto.CategoryCreateDTO;
import com.bookmall.category.dto.CategoryDTO;
import com.bookmall.category.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author BookMall Team
 */
public interface CategoryService {

    /**
     * 获取所有分类列表（含教材数量）
     */
    List<CategoryDTO> getAllCategories();

    /**
     * 获取所有分类（简单列表，不含数量）
     */
    List<Category> getAllCategoriesSimple();

    /**
     * 根据ID获取分类详情
     */
    CategoryDTO getCategoryById(Long categoryId);

    /**
     * 根据ID获取分类实体
     */
    Category getCategoryEntityById(Long categoryId);

    /**
     * 创建分类
     */
    CategoryDTO createCategory(CategoryCreateDTO request);

    /**
     * 更新分类
     */
    CategoryDTO updateCategory(Long categoryId, CategoryCreateDTO request);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);

    /**
     * 批量删除分类
     */
    void batchDeleteCategories(List<Long> categoryIds);

    /**
     * 更新排序
     */
    void updateSortOrder(Long categoryId, Integer sortOrder);

    /**
     * 检查分类是否存在
     */
    boolean existsById(Long categoryId);

    /**
     * 获取分类下的教材数量
     */
    Integer getBookCountByCategoryId(Long categoryId);
}