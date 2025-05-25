package com.restful.restaurant.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddReviewRequest {
    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating cannot be less than 0")
    @Max(value = 5, message = "Rating cannot be more 5")
    private Integer rating;
    @NotBlank(message = "Review is required")
    private String review;
    @NotNull(message = "Restaurant ID is required")
    private long restaurantId;
}
