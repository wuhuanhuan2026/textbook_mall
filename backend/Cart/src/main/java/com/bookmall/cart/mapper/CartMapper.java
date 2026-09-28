package com.bookmall.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmall.cart.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 购物车数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 根据用户ID查询购物车列表（含教材信息）
     */
    List<Cart> selectCartListByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和教材ID查询购物车项
     */
    @Select("SELECT cart_id, user_id, book_id, quantity, create_time, update_time " +
            "FROM tb_cart WHERE user_id = #{userId} AND book_id = #{bookId}")
    Cart selectByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 更新购物车数量
     */
    @Update("UPDATE tb_cart SET quantity = quantity + #{quantity} " +
            "WHERE user_id = #{userId} AND book_id = #{bookId}")
    int incrementQuantity(@Param("userId") Long userId,
                          @Param("bookId") Long bookId,
                          @Param("quantity") Integer quantity);

    /**
     * 统计用户购物车商品数量
     */
    @Select("SELECT SUM(quantity) FROM tb_cart WHERE user_id = #{userId}")
    Integer sumQuantityByUserId(@Param("userId") Long userId);

    /**
     * 统计用户购物车商品种类数
     */
    @Select("SELECT COUNT(*) FROM tb_cart WHERE user_id = #{userId}")
    Integer countByUserId(@Param("userId") Long userId);

    /**
     * ✅ 修复：将 @Select 改为 @Delete
     * 清空用户购物车，返回删除行数
     */
    @Delete("DELETE FROM tb_cart WHERE user_id = #{userId}")
    int clearByUserId(@Param("userId") Long userId);

    /**
     * 批量删除购物车项
     */
    int batchDelete(@Param("cartIds") List<Long> cartIds);

    /**
     * 批量删除用户购物车（用户注销时使用）
     */
    int batchDeleteByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 获取用户购物车选中的商品总金额
     */
    @Select("SELECT SUM(c.quantity * b.price) " +
            "FROM tb_cart c " +
            "INNER JOIN tb_book b ON c.book_id = b.book_id " +
            "WHERE c.user_id = #{userId} AND b.status = 1")
    java.math.BigDecimal sumTotalAmountByUserId(@Param("userId") Long userId);
}