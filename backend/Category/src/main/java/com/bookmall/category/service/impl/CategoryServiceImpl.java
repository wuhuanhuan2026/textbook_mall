package com.bookmall.category.service.impl;

import com.bookmall.category.dto.CategoryCreateDTO;
import com.bookmall.category.dto.CategoryDTO;
import com.bookmall.category.entity.Category;
import com.bookmall.category.mapper.CategoryMapper;
import com.bookmall.category.service.CategoryService;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final RedisUtil redisUtil;

    private static final String REDIS_CATEGORY_KEY = "category:list";

    @Override
    public List<CategoryDTO> getAllCategories() {
        // 尝试从Redis获取缓存
        Object cached = redisUtil.get(REDIS_CATEGORY_KEY);
        if (cached != null) {
            try {
                @SuppressWarnings("unchecked")
                List<CategoryDTO> cachedList = (List<CategoryDTO>) cached;
                return cachedList;
            } catch (Exception e) {
                log.warn("Redis缓存解析失败，从数据库查询");
            }
        }

        // 从数据库查询
        List<Category> categories = categoryMapper.selectAllWithBookCount();
        List<CategoryDTO> result = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 缓存到Redis（5分钟过期）
        redisUtil.set(REDIS_CATEGORY_KEY, result, 5, java.util.concurrent.TimeUnit.MINUTES);

        return result;
    }

    @Override
    public List<Category> getAllCategoriesSimple() {
        return categoryMapper.selectAllOrderBySort();
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = getCategoryEntityById(categoryId);
        return convertToDTO(category);
    }

    @Override
    public Category getCategoryEntityById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ExceptionEnum.CATEGORY_NOT_EXIST);
        }
        return category;
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryCreateDTO request) {
        // 检查分类名称是否已存在
        Category existCategory = categoryMapper.selectByCategoryName(request.getCategoryName());
        if (existCategory != null) {
            throw new BusinessException("分类名称已存在");
        }

        // 创建分类
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setSortOrder(request.getSortOrder());

        categoryMapper.insert(category);

        // 清除Redis缓存
        redisUtil.delete(REDIS_CATEGORY_KEY);

        log.info("创建分类成功: categoryId={}, categoryName={}",
                category.getCategoryId(), category.getCategoryName());

        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryCreateDTO request) {
        Category category = getCategoryEntityById(categoryId);

        // 检查分类名称是否被其他分类使用
        int count = categoryMapper.countByNameExcludeId(request.getCategoryName(), categoryId);
        if (count > 0) {
            throw new BusinessException("分类名称已被其他分类使用");
        }

        // 更新
        category.setCategoryName(request.getCategoryName());
        category.setSortOrder(request.getSortOrder());
        categoryMapper.updateById(category);

        // 清除Redis缓存
        redisUtil.delete(REDIS_CATEGORY_KEY);

        log.info("更新分类成功: categoryId={}, categoryName={}",
                categoryId, request.getCategoryName());

        return convertToDTO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryEntityById(categoryId);

        // 检查分类下是否有教材
        int bookCount = categoryMapper.countBooksByCategoryId(categoryId);
        if (bookCount > 0) {
            throw new BusinessException(ExceptionEnum.CATEGORY_HAS_BOOKS);
        }

        categoryMapper.deleteById(categoryId);

        // 清除Redis缓存
        redisUtil.delete(REDIS_CATEGORY_KEY);

        log.info("删除分类成功: categoryId={}, categoryName={}",
                categoryId, category.getCategoryName());
    }

    @Override
    @Transactional
    public void batchDeleteCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        // 检查每个分类下是否有教材
        for (Long categoryId : categoryIds) {
            int bookCount = categoryMapper.countBooksByCategoryId(categoryId);
            if (bookCount > 0) {
                Category category = categoryMapper.selectById(categoryId);
                throw new BusinessException("分类【" + category.getCategoryName() + "】下存在教材，无法删除");
            }
        }

        categoryMapper.batchDelete(categoryIds);

        // 清除Redis缓存
        redisUtil.delete(REDIS_CATEGORY_KEY);

        log.info("批量删除分类成功: categoryIds={}", categoryIds);
    }

    @Override
    @Transactional
    public void updateSortOrder(Long categoryId, Integer sortOrder) {
        Category category = getCategoryEntityById(categoryId);
        category.setSortOrder(sortOrder);
        categoryMapper.updateById(category);

        // 清除Redis缓存
        redisUtil.delete(REDIS_CATEGORY_KEY);

        log.info("更新分类排序成功: categoryId={}, sortOrder={}", categoryId, sortOrder);
    }

    @Override
    public boolean existsById(Long categoryId) {
        return categoryMapper.selectById(categoryId) != null;
    }

    @Override
    public Integer getBookCountByCategoryId(Long categoryId) {
        return categoryMapper.countBooksByCategoryId(categoryId);
    }

    // ==================== 私有方法 ====================

    /**
     * 实体转DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .sortOrder(category.getSortOrder())
                .bookCount(category.getBookCount() != null ? category.getBookCount() : 0)
                .build();
    }
}