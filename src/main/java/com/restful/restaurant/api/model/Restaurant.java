package com.restful.restaurant.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long restaurantId;
    private String restaurantName;
    private int restaurantCategory;
    private String restaurantPictureUrl;

    public Restaurant(String restaurantName, int restaurantCategory) {
        this.restaurantName = restaurantName;
        this.restaurantCategory = restaurantCategory;
    }

    public Restaurant(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Restaurant() {

    }
}
