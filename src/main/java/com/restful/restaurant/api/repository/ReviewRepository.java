package com.restful.restaurant.api.repository;

import com.restful.restaurant.api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurant_RestaurantId(Long restaurantId);
}
