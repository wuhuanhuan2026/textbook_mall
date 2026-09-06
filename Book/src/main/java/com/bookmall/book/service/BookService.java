package com.bookmall.book.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.book.dto.*;
import com.bookmall.book.entity.Book;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教材服务接口
 *
 * @author BookMall Team
 */
public interface BookService {

    // ==================== 查询 ====================

    /**
     * 分页查询教材列表
     */
    Page<BookDTO> getBookList(BookSearchDTO searchDTO);

    /**
     * 获取教材详情
     */
    BookDTO getBookDetail(Long bookId);

    /**
     * 获取教材实体
     */
    Book getBookEntity(Long bookId);

    /**
     * 获取热门教材
     */
    List<BookDTO> getHotBooks(int limit);

    /**
     * 获取新品教材
     */
    List<BookDTO> getNewBooks(int limit);

    /**
     * 按分类获取教材
     */
    Page<BookDTO> getBooksByCategory(Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 搜索教材
     */
    Page<BookDTO> searchBooks(String keyword, Integer pageNum, Integer pageSize);

    // ==================== 管理 ====================

    /**
     * 创建教材
     */
    BookDTO createBook(BookCreateDTO request);

    /**
     * 更新教材
     */
    BookDTO updateBook(Long bookId, BookUpdateDTO request);

    /**
     * 删除教材
     */
    void deleteBook(Long bookId);

    /**
     * 批量删除教材
     */
    void batchDeleteBooks(List<Long> bookIds);

    /**
     * 上架
     */
    void onSale(Long bookId);

    /**
     * 下架
     */
    void offSale(Long bookId);

    /**
     * 批量上架
     */
    void batchOnSale(List<Long> bookIds);

    /**
     * 批量下架
     */
    void batchOffSale(List<Long> bookIds);

    // ==================== 库存操作 ====================

    /**
     * 扣减库存（订单使用）
     */
    void deductStock(Long bookId, Integer quantity);

    /**
     * 增加库存
     */
    void addStock(Long bookId, Integer quantity);

    /**
     * 增加销量（订单使用）
     */
    void addSales(Long bookId, Integer quantity);

    /**
     * 获取库存
     */
    Integer getStock(Long bookId);

    /**
     * 检查库存是否充足
     */
    boolean checkStock(Long bookId, Integer quantity);

    // ==================== 验证 ====================

    /**
     * 检查教材是否存在
     */
    boolean existsById(Long bookId);

    /**
     * 检查教材是否上架
     */
    boolean isOnSale(Long bookId);

    // ==================== 统计 ====================

    /**
     * 获取教材统计信息
     */
    BookStatisticsVO getStatistics();
}