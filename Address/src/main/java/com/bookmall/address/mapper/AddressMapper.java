package com.bookmall.address.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmall.address.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 收货地址数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    /**
     * 根据用户ID查询所有地址（按创建时间降序）
     */
    @Select("SELECT address_id, user_id, receiver_name, receiver_phone, " +
            "province, city, district, detail_address, is_default, create_time " +
            "FROM tb_address WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Address> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的所有地址（默认地址排最前）
     */
    @Select("SELECT address_id, user_id, receiver_name, receiver_phone, " +
            "province, city, district, detail_address, is_default, create_time " +
            "FROM tb_address WHERE user_id = #{userId} " +
            "ORDER BY is_default DESC, create_time DESC")
    List<Address> selectByUserIdWithDefaultFirst(@Param("userId") Long userId);

    /**
     * 获取用户的默认地址
     */
    @Select("SELECT address_id, user_id, receiver_name, receiver_phone, " +
            "province, city, district, detail_address, is_default, create_time " +
            "FROM tb_address WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Address selectDefaultByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否存在默认地址
     */
    @Select("SELECT COUNT(*) FROM tb_address WHERE user_id = #{userId} AND is_default = 1")
    int countDefaultByUserId(@Param("userId") Long userId);

    /**
     * 取消用户的所有默认地址
     */
    @Update("UPDATE tb_address SET is_default = 0 WHERE user_id = #{userId} AND is_default = 1")
    int clearDefaultByUserId(@Param("userId") Long userId);

    /**
     * 设置指定地址为默认
     */
    @Update("UPDATE tb_address SET is_default = 1 " +
            "WHERE address_id = #{addressId} AND user_id = #{userId}")
    int setDefault(@Param("addressId") Long addressId, @Param("userId") Long userId);

    /**
     * 统计用户的地址数量
     */
    @Select("SELECT COUNT(*) FROM tb_address WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 批量删除用户的地址
     */
    int batchDeleteByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 根据用户ID和地址ID查询
     */
    @Select("SELECT address_id, user_id, receiver_name, receiver_phone, " +
            "province, city, district, detail_address, is_default, create_time " +
            "FROM tb_address WHERE address_id = #{addressId} AND user_id = #{userId}")
    Address selectByUserIdAndAddressId(@Param("userId") Long userId,
                                       @Param("addressId") Long addressId);

    /**
     * 获取用户的地址数量（用于限制）
     */
    @Select("SELECT COUNT(*) FROM tb_address WHERE user_id = #{userId}")
    int countAddressesByUserId(@Param("userId") Long userId);

    /**
     * 检查地址是否属于用户
     */
    @Select("SELECT COUNT(*) FROM tb_address WHERE address_id = #{addressId} AND user_id = #{userId}")
    int countByAddressIdAndUserId(@Param("addressId") Long addressId,
                                  @Param("userId") Long userId);
}