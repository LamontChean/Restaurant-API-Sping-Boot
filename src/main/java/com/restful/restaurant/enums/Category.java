package com.restful.restaurant.enums;

import lombok.Getter;

@Getter
public enum Category {
    FAST_FOOD(1),
    CAFE(2),
    FINE_DINING(3),
    CASUAL_DINING(4);

    private final int code;

    Category(int code) {
        this.code = code;
    }

    public static Category fromCode(int code) {
        for (Category c : Category.values()) {
            if (c.getCode() == code) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid category code: " + code);
    }

    public static boolean isValidCode(int code) {
        return code > 0 && code <= Category.values().length;
    }
}
