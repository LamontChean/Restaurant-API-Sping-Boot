package com.restful.restaurant.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restful.restaurant.api.common.CommonResponse;
import com.restful.restaurant.api.dto.AddRestaurantRequest;
import com.restful.restaurant.api.dto.AddReviewRequest;
import com.restful.restaurant.api.exception.InvalidCategoryException;
import com.restful.restaurant.api.exception.NoRatingException;
import com.restful.restaurant.api.exception.RestaurantNotFoundException;
import com.restful.restaurant.api.model.Restaurant;
import com.restful.restaurant.api.service.RestaurantService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    @GetMapping("/all")
    public CommonResponse<List<Restaurant>> getAllRestaurants() {
        return new CommonResponse<>(restaurantService.getAllRestaurants());
    }

    @GetMapping("/get")
    public CommonResponse<List<Restaurant>> getRestaurantsByNameOrCategory(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer category) throws InvalidCategoryException {
        return new CommonResponse<>(restaurantService.searchRestaurants(name, category));
    }


    @GetMapping("/getAvgRate")
    public CommonResponse<Double> getRestaurantAverageRate(@RequestParam Long restaurantId) throws RestaurantNotFoundException, NoRatingException {

        return new CommonResponse<>(restaurantService.calculateAverageRating(restaurantId));
    }

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<Object> addRestaurant(
            @Parameter(
                    description = "Exp: {\"restaurantName\":\"restaurant\",\"restaurantCategory\": 1}"
            )
            @RequestPart("restaurant") @Valid String request,
            @RequestPart("picture") MultipartFile picture) throws IOException, InvalidCategoryException {

        AddRestaurantRequest addRestaurantRequest = new ObjectMapper().readValue(request, AddRestaurantRequest.class);

        return new CommonResponse<>(restaurantService.addRestaurant(addRestaurantRequest, picture));
    }


    @PostMapping("/rate")
    public CommonResponse<Boolean> addRestaurantReview(@RequestBody @Valid AddReviewRequest request) throws RestaurantNotFoundException {

        return new CommonResponse<Boolean>(restaurantService.addReview(request));
    }
}
