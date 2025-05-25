package com.restful.restaurant.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewId;
    private String review;
    private int rating;
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public Review(String review, int rating, Restaurant restaurant) {
        this.review = review;
        this.rating = rating;
        this.restaurant = restaurant;
    }

    public Review() {

    }
}
