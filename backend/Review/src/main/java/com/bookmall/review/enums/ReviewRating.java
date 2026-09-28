package com.bookmall.review.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评价星级枚举
 *
 * @author BookMall Team
 */
@Getter
@AllArgsConstructor
public enum ReviewRating {

    ONE_STAR(1, "⭐", "很差"),
    TWO_STARS(2, "⭐⭐", "较差"),
    THREE_STARS(3, "⭐⭐⭐", "一般"),
    FOUR_STARS(4, "⭐⭐⭐⭐", "满意"),
    FIVE_STARS(5, "⭐⭐⭐⭐⭐", "非常满意");

    private final int code;
    private final String stars;
    private final String desc;

    public static ReviewRating fromCode(int code) {
        for (ReviewRating rating : values()) {
            if (rating.code == code) {
                return rating;
            }
        }
        return null;
    }

    public static String getStars(int code) {
        ReviewRating rating = fromCode(code);
        return rating != null ? rating.stars : "";
    }

    public static String getDesc(int code) {
        ReviewRating rating = fromCode(code);
        return rating != null ? rating.desc : "未知";
    }

    /**
     * 校验评分是否合法
     */
    public static boolean isValid(int code) {
        return code >= 1 && code <= 5;
    }
}
