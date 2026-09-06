package com.bookmall.book.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.book.dto.*;
import com.bookmall.book.entity.Book;
import com.bookmall.book.mapper.BookMapper;
import com.bookmall.book.service.BookService;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 教材服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final RedisUtil redisUtil;

    // ==================== 查询 ====================

    @Override
    public Page<BookDTO> getBookList(BookSearchDTO searchDTO) {
        // 构建分页对象
        Page<Book> page = new Page<>(searchDTO.getPageNum(), searchDTO.getPageSize());

        // 执行查询
        Page<Book> bookPage = bookMapper.selectBookPage(
                page,
                searchDTO.getKeyword(),
                searchDTO.getCategoryId(),
                searchDTO.getMinPrice(),
                searchDTO.getMaxPrice(),
                searchDTO.getStatus(),
                searchDTO.getSortBy()
        );

        // 转换为DTO
        return convertPage(bookPage);
    }

    @Override
    public BookDTO getBookDetail(Long bookId) {
        // 先从Redis缓存获取
        String cacheKey = Constants.REDIS_BOOK_CACHE + bookId;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            try {
                return (BookDTO) cached;
            } catch (Exception e) {
                log.warn("Redis缓存解析失败，从数据库查询");
            }
        }

        // 从数据库查询
        Book book = bookMapper.selectBookDetailById(bookId);
        if (book == null) {
            throw new BusinessException(ExceptionEnum.BOOK_NOT_EXIST);
        }

        BookDTO dto = convertToDTO(book);

        // 缓存到Redis（30分钟过期）
        redisUtil.set(cacheKey, dto, 30, TimeUnit.MINUTES);

        return dto;
    }

    @Override
    public Book getBookEntity(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(ExceptionEnum.BOOK_NOT_EXIST);
        }
        return book;
    }

    @Override
    public List<BookDTO> getHotBooks(int limit) {
        List<Book> books = bookMapper.selectHotBooks(limit);
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getNewBooks(int limit) {
        List<Book> books = bookMapper.selectNewBooks(limit);
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookDTO> getBooksByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        Page<Book> bookPage = bookMapper.selectBooksByCategory(page, categoryId, Constants.BOOK_STATUS_ON);
        return convertPage(bookPage);
    }

    @Override
    public Page<BookDTO> searchBooks(String keyword, Integer pageNum, Integer pageSize) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        Page<Book> bookPage = bookMapper.searchBooks(page, keyword, null);
        return convertPage(bookPage);
    }

    // ==================== 管理 ====================

    @Override
    @Transactional
    public BookDTO createBook(BookCreateDTO request) {
        // 检查ISBN是否已存在
        if (StringUtils.hasText(request.getIsbn())) {
            Book existBook = bookMapper.selectByIsbn(request.getIsbn());
            if (existBook != null) {
                throw new BusinessException("ISBN已存在");
            }
        }

        // 创建教材
        Book book = new Book();
        book.setBookName(request.getBookName());
        book.setIsbn(request.getIsbn());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        book.setOriginalPrice(request.getOriginalPrice());
        book.setStock(request.getStock() != null ? request.getStock() : 0);
        book.setCoverImage(request.getCoverImage());
        book.setDescription(request.getDescription());
        book.setStatus(request.getStatus() != null ? request.getStatus() : Constants.BOOK_STATUS_ON);
        book.setSales(0);

        bookMapper.insert(book);

        log.info("创建教材成功: bookId={}, bookName={}", book.getBookId(), book.getBookName());

        return convertToDTO(book);
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long bookId, BookUpdateDTO request) {
        Book book = getBookEntity(bookId);

        // 检查ISBN是否被其他教材使用
        if (StringUtils.hasText(request.getIsbn())) {
            int count = bookMapper.countByIsbnExcludeId(request.getIsbn(), bookId);
            if (count > 0) {
                throw new BusinessException("ISBN已被其他教材使用");
            }
        }

        // 更新
        book.setBookName(request.getBookName());
        book.setIsbn(request.getIsbn());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setCategoryId(request.getCategoryId());
        book.setPrice(request.getPrice());
        book.setOriginalPrice(request.getOriginalPrice());
        book.setStock(request.getStock());
        book.setCoverImage(request.getCoverImage());
        book.setDescription(request.getDescription());
        book.setStatus(request.getStatus());

        bookMapper.updateById(book);

        // 清除Redis缓存
        redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);

        log.info("更新教材成功: bookId={}, bookName={}", bookId, book.getBookName());

        return convertToDTO(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        Book book = getBookEntity(bookId);
        bookMapper.deleteById(bookId);

        // 清除Redis缓存
        redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);

        log.info("删除教材成功: bookId={}, bookName={}", bookId, book.getBookName());
    }

    @Override
    @Transactional
    public void batchDeleteBooks(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return;
        }
        bookMapper.batchDelete(bookIds);

        // 清除Redis缓存
        for (Long bookId : bookIds) {
            redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);
        }

        log.info("批量删除教材成功: bookIds={}", bookIds);
    }

    @Override
    @Transactional
    public void onSale(Long bookId) {
        Book book = getBookEntity(bookId);
        if (book.getStatus() == Constants.BOOK_STATUS_ON) {
            throw new BusinessException("教材已上架");
        }
        bookMapper.onSale(bookId);

        // 清除Redis缓存
        redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);

        log.info("上架教材成功: bookId={}", bookId);
    }

    @Override
    @Transactional
    public void offSale(Long bookId) {
        Book book = getBookEntity(bookId);
        if (book.getStatus() == Constants.BOOK_STATUS_OFF) {
            throw new BusinessException("教材已下架");
        }
        bookMapper.offSale(bookId);

        // 清除Redis缓存
        redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);

        log.info("下架教材成功: bookId={}", bookId);
    }

    @Override
    @Transactional
    public void batchOnSale(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return;
        }
        bookMapper.batchOnSale(bookIds);

        // 清除Redis缓存
        for (Long bookId : bookIds) {
            redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);
        }

        log.info("批量上架教材成功: bookIds={}", bookIds);
    }

    @Override
    @Transactional
    public void batchOffSale(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return;
        }
        bookMapper.batchOffSale(bookIds);

        // 清除Redis缓存
        for (Long bookId : bookIds) {
            redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);
        }

        log.info("批量下架教材成功: bookIds={}", bookIds);
    }

    // ==================== 库存操作 ====================

    @Override
    @Transactional
    public void deductStock(Long bookId, Integer quantity) {
        int result = bookMapper.deductStock(bookId, quantity);
        if (result <= 0) {
            throw new BusinessException(ExceptionEnum.STOCK_INSUFFICIENT);
        }

        // 清除Redis缓存
        redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);

        log.info("扣减库存成功: bookId={}, quantity={}", bookId, quantity);
    }

    @Override
    @Transactional
    public void addStock(Long bookId, Integer quantity) {
        bookMapper.addStock(bookId, quantity);

        // 清除Redis缓存
        redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);

        log.info("增加库存成功: bookId={}, quantity={}", bookId, quantity);
    }

    @Override
    @Transactional
    public void addSales(Long bookId, Integer quantity) {
        bookMapper.addSales(bookId, quantity);

        // 清除Redis缓存
        redisUtil.delete(Constants.REDIS_BOOK_CACHE + bookId);
    }

    @Override
    public Integer getStock(Long bookId) {
        return bookMapper.getStock(bookId);
    }

    @Override
    public boolean checkStock(Long bookId, Integer quantity) {
        Integer stock = getStock(bookId);
        return stock != null && stock >= quantity;
    }

    // ==================== 验证 ====================

    @Override
    public boolean existsById(Long bookId) {
        return bookMapper.selectById(bookId) != null;
    }

    @Override
    public boolean isOnSale(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        return book != null && book.getStatus() == Constants.BOOK_STATUS_ON;
    }

    // ==================== 统计 ====================

    @Override
    public BookStatisticsVO getStatistics() {
        Long totalBooks = bookMapper.countTotal();
        Long onSaleBooks = bookMapper.countOnSale();
        Long offSaleBooks = totalBooks - onSaleBooks;
        Long totalStock = bookMapper.sumStock();
        Long totalSales = bookMapper.sumSales();
        Map<String, BigDecimal> priceStats = bookMapper.getPriceStats();

        return BookStatisticsVO.builder()
                .totalBooks(totalBooks)
                .onSaleBooks(onSaleBooks)
                .offSaleBooks(offSaleBooks)
                .totalStock(totalStock != null ? totalStock : 0)
                .totalSales(totalSales != null ? totalSales : 0)
                .avgPrice(priceStats != null ? priceStats.get("avgPrice") : BigDecimal.ZERO)
                .maxPrice(priceStats != null ? priceStats.get("maxPrice") : BigDecimal.ZERO)
                .minPrice(priceStats != null ? priceStats.get("minPrice") : BigDecimal.ZERO)
                .build();
    }

    // ==================== 私有方法 ====================

    /**
     * 实体转DTO
     */
    private BookDTO convertToDTO(Book book) {
        if (book == null) {
            return null;
        }
        return BookDTO.builder()
                .bookId(book.getBookId())
                .bookName(book.getBookName())
                .isbn(book.getIsbn())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .categoryId(book.getCategoryId())
                .categoryName(book.getCategoryName())
                .price(book.getPrice())
                .originalPrice(book.getOriginalPrice())
                .stock(book.getStock())
                .coverImage(book.getCoverImage())
                .description(book.getDescription())
                .status(book.getStatus())
                .sales(book.getSales())
                .avgRating(book.getAvgRating())
                .createTime(book.getCreateTime())
                .updateTime(book.getUpdateTime())
                .build();
    }

    /**
     * 分页结果转换
     */
    private Page<BookDTO> convertPage(Page<Book> page) {
        Page<BookDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return dtoPage;
    }
}