package com.bookmall.category.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体类 - 对应 tb_category 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_category")
public class Category {

    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    @TableField("category_name")
    private String categoryName;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 该分类下的教材数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer bookCount;
}