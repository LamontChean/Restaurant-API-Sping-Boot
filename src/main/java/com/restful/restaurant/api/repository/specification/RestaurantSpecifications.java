package com.restful.restaurant.api.repository.specification;

import com.restful.restaurant.api.model.Restaurant;
import org.springframework.data.jpa.domain.Specification;

public class RestaurantSpecifications {

    public static Specification<Restaurant> hasNameLike(String name) {
        return (root, query, builder) -> {
            if (name == null || name.trim().isEmpty()) {
                return builder.conjunction(); // always true
            }
            return builder.like(builder.lower(root.get("restaurantName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Restaurant> hasCategory(Integer category) {
        return (root, query, builder) -> {
            if (category == null) {
                return builder.conjunction(); // always true
            }
            return builder.equal(root.get("restaurantCategory"), category);
        };
    }
}