package com.restful.restaurant.api.dto;

import lombok.Data;

@Data
public class AddRestaurantRequest {
    private String restaurantName;

    private int restaurantCategory;

}
