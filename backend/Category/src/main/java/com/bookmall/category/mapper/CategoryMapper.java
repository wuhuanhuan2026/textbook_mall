package com.bookmall.category.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmall.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 分类数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有分类（按排序升序）
     */
    @Select("SELECT category_id, category_name, sort_order, create_time " +
            "FROM tb_category ORDER BY sort_order ASC")
    List<Category> selectAllOrderBySort();

    /**
     * 查询所有分类并统计教材数量
     */
    List<Category> selectAllWithBookCount();

    /**
     * 根据分类名称查询
     */
    @Select("SELECT category_id, category_name, sort_order, create_time " +
            "FROM tb_category WHERE category_name = #{categoryName}")
    Category selectByCategoryName(@Param("categoryName") String categoryName);

    /**
     * 检查分类名称是否存在（排除自身）
     */
    @Select("SELECT COUNT(*) FROM tb_category " +
            "WHERE category_name = #{categoryName} AND category_id != #{categoryId}")
    int countByNameExcludeId(@Param("categoryName") String categoryName,
                             @Param("categoryId") Long categoryId);

    /**
     * 统计该分类下的教材数量
     */
    @Select("SELECT COUNT(*) FROM tb_book WHERE category_id = #{categoryId} AND status = 1")
    int countBooksByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 批量删除分类（物理删除，XML实现）
     */
    int batchDelete(@Param("categoryIds") List<Long> categoryIds);

    /**
     * 批量更新排序（XML实现）
     */
    int batchUpdateSort(@Param("list") List<Category> categories);

    /**
     * 获取最大排序值
     */
    @Select("SELECT MAX(sort_order) FROM tb_category")
    Integer getMaxSortOrder();

    /**
     * 更新分类名称
     */
    @Update("UPDATE tb_category SET category_name = #{categoryName} " +
            "WHERE category_id = #{categoryId}")
    int updateName(@Param("categoryId") Long categoryId,
                   @Param("categoryName") String categoryName);

    /**
     * 更新排序
     */
    @Update("UPDATE tb_category SET sort_order = #{sortOrder} " +
            "WHERE category_id = #{categoryId}")
    int updateSortOrder(@Param("categoryId") Long categoryId,
                        @Param("sortOrder") Integer sortOrder);
}