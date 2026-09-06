package com.bookmall.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 教材列表响应VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "教材列表响应")
public class BookListVO {

    @Schema(description = "教材列表")
    private List<BookDTO> list;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页大小")
    private Integer pageSize;

    @Schema(description = "总页数")
    private Integer totalPages;
}