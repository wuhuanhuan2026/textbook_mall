package com.bookmall.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教材数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    // ==================== 基础查询 ====================

    /**
     * 根据ID查询教材（含分类名称）
     */
    Book selectBookDetailById(@Param("bookId") Long bookId);

    /**
     * 根据ISBN查询教材
     */
    @Select("SELECT book_id, book_name, isbn, author, publisher, category_id, " +
            "price, original_price, stock, cover_image, description, status, sales, create_time, update_time " +
            "FROM tb_book WHERE isbn = #{isbn}")
    Book selectByIsbn(@Param("isbn") String isbn);

    /**
     * 查询所有上架教材
     */
    @Select("SELECT book_id, book_name, author, price, cover_image, sales, stock " +
            "FROM tb_book WHERE status = 1 ORDER BY book_id")
    List<Book> selectAllOnSale();

    // ==================== 分页查询 ====================

    /**
     * 分页查询教材列表
     */
    Page<Book> selectBookPage(Page<Book> page,
                              @Param("keyword") String keyword,
                              @Param("categoryId") Long categoryId,
                              @Param("minPrice") BigDecimal minPrice,
                              @Param("maxPrice") BigDecimal maxPrice,
                              @Param("status") Integer status,
                              @Param("sortBy") String sortBy);

    /**
     * 按分类分页查询教材
     */
    Page<Book> selectBooksByCategory(Page<Book> page,
                                     @Param("categoryId") Long categoryId,
                                     @Param("status") Integer status);

    /**
     * 搜索教材
     */
    Page<Book> searchBooks(Page<Book> page,
                           @Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId);

    // ==================== 热门/新品 ====================

    /**
     * 获取热门教材（按销量排序）
     */
    @Select("SELECT book_id, book_name, author, price, original_price, cover_image, sales, stock " +
            "FROM tb_book WHERE status = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<Book> selectHotBooks(@Param("limit") int limit);

    /**
     * 获取新品教材（按创建时间排序）
     */
    @Select("SELECT book_id, book_name, author, price, original_price, cover_image, sales, stock " +
            "FROM tb_book WHERE status = 1 ORDER BY create_time DESC LIMIT #{limit}")
    List<Book> selectNewBooks(@Param("limit") int limit);

    // ==================== 库存操作 ====================

    /**
     * 扣减库存
     */
    @Update("UPDATE tb_book SET stock = stock - #{quantity} " +
            "WHERE book_id = #{bookId} AND stock >= #{quantity} AND status = 1")
    int deductStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    /**
     * 增加库存
     */
    @Update("UPDATE tb_book SET stock = stock + #{quantity} " +
            "WHERE book_id = #{bookId}")
    int addStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    /**
     * 增加销量
     */
    @Update("UPDATE tb_book SET sales = sales + #{quantity} " +
            "WHERE book_id = #{bookId}")
    int addSales(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    /**
     * 获取库存
     */
    @Select("SELECT stock FROM tb_book WHERE book_id = #{bookId}")
    Integer getStock(@Param("bookId") Long bookId);

    // ==================== 状态操作 ====================

    /**
     * 上架
     */
    @Update("UPDATE tb_book SET status = 1 WHERE book_id = #{bookId}")
    int onSale(@Param("bookId") Long bookId);

    /**
     * 下架
     */
    @Update("UPDATE tb_book SET status = 0 WHERE book_id = #{bookId}")
    int offSale(@Param("bookId") Long bookId);

    /**
     * 批量上架
     */
    int batchOnSale(@Param("bookIds") List<Long> bookIds);

    /**
     * 批量下架
     */
    int batchOffSale(@Param("bookIds") List<Long> bookIds);

    // ==================== 统计查询 ====================

    /**
     * 统计各分类教材数量
     */
    @Select("SELECT category_id, COUNT(*) AS count FROM tb_book " +
            "WHERE status = 1 GROUP BY category_id")
    List<java.util.Map<String, Object>> countByCategory();

    /**
     * 统计教材总数
     */
    @Select("SELECT COUNT(*) FROM tb_book")
    Long countTotal();

    /**
     * 统计上架教材数
     */
    @Select("SELECT COUNT(*) FROM tb_book WHERE status = 1")
    Long countOnSale();

    /**
     * 统计总库存
     */
    @Select("SELECT SUM(stock) FROM tb_book WHERE status = 1")
    Long sumStock();

    /**
     * 统计总销量
     */
    @Select("SELECT SUM(sales) FROM tb_book WHERE status = 1")
    Long sumSales();

    /**
     * 统计价格信息
     */
    @Select("SELECT AVG(price) AS avgPrice, MAX(price) AS maxPrice, MIN(price) AS minPrice " +
            "FROM tb_book WHERE status = 1")
    java.util.Map<String, BigDecimal> getPriceStats();

    /**
     * 统计指定分类的教材数量
     */
    @Select("SELECT COUNT(*) FROM tb_book WHERE category_id = #{categoryId} AND status = 1")
    int countByCategoryId(@Param("categoryId") Long categoryId);

    // ==================== 批量操作 ====================

    /**
     * 批量删除（物理删除）
     */
    int batchDelete(@Param("bookIds") List<Long> bookIds);

    /**
     * 批量更新分类
     */
    @Update("<script>" +
            "UPDATE tb_book SET category_id = #{categoryId} " +
            "WHERE book_id IN " +
            "<foreach collection='bookIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateCategory(@Param("bookIds") List<Long> bookIds,
                            @Param("categoryId") Long categoryId);

    /**
     * 检查是否存在相同ISBN（排除自身）
     */
    @Select("SELECT COUNT(*) FROM tb_book WHERE isbn = #{isbn} AND book_id != #{bookId}")
    int countByIsbnExcludeId(@Param("isbn") String isbn, @Param("bookId") Long bookId);
}