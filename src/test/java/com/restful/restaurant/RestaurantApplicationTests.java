package com.restful.restaurant;

import com.restful.restaurant.api.dto.AddRestaurantRequest;
import com.restful.restaurant.api.dto.AddReviewRequest;
import com.restful.restaurant.api.exception.InvalidCategoryException;
import com.restful.restaurant.api.exception.InvalidCredentialsException;
import com.restful.restaurant.api.exception.NoRatingException;
import com.restful.restaurant.api.exception.RestaurantNotFoundException;
import com.restful.restaurant.api.model.Restaurant;
import com.restful.restaurant.api.repository.RestaurantRepository;
import com.restful.restaurant.api.repository.ReviewRepository;
import com.restful.restaurant.api.service.AuthService;
import com.restful.restaurant.api.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RestaurantApplicationTests {

    @Autowired
    AuthService authService;
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @ParameterizedTest
    @CsvSource({
            "user, user, true",
            "test, wrong, false",
            " , wrong, false",
            "wrong, , false",
            " , , false"
    })
    void testAuthorize(String username, String password, boolean expectSuccess) throws InvalidCredentialsException {
        if (expectSuccess) {
            String token = authService.login(username, password);
            assertNotNull(token);
            assertFalse(token.isEmpty());
        } else {
            assertThrows(Exception.class, () -> authService.login(username, password));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Sample Restaurant, 1, true",
            ", 1, false",
            "Sample Restaurant, , false",
            "Sample Restaurant, 5, false",
            " , , false"
    })
    void testAddRestaurant(String restaurantName, Integer restaurantCategory, boolean expectSuccess) throws IOException, InvalidCategoryException {
        MultipartFile picture = new MockMultipartFile(
                "picture",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        // Prepare request
        AddRestaurantRequest request = new AddRestaurantRequest();
        request.setRestaurantName(restaurantName);
        request.setRestaurantCategory(restaurantCategory == null ? 0 : restaurantCategory);

        if (expectSuccess) {
            Restaurant savedRestaurant = restaurantService.addRestaurant(request, picture);
            assertNotNull(savedRestaurant);
        } else {
            assertThrows(Exception.class, () -> restaurantService.addRestaurant(request, picture));
        }
    }

    @Test
    void testGetAllRestaurants() throws IOException, InvalidCategoryException {
        List<Restaurant> currentRestaurant = restaurantService.getAllRestaurants();
        assertNotNull(currentRestaurant);
        int currentRestaurantSize = currentRestaurant.size();

        MultipartFile picture = new MockMultipartFile(
                "picture",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        // Prepare request
        AddRestaurantRequest request = new AddRestaurantRequest();
        request.setRestaurantName("My Test Restaurant");
        request.setRestaurantCategory(1);

        Restaurant savedRestaurant = restaurantService.addRestaurant(request, picture);
        assertNotNull(savedRestaurant);

        currentRestaurant = restaurantService.getAllRestaurants();
        assertEquals(currentRestaurant.size(), currentRestaurantSize + 1);
    }

    @ParameterizedTest
    @CsvSource({
            "Test, 1, true",                 // Valid name and category
            ", 1, true",                     // Empty name, valid category
            "Test, , true",                 // Simulated null category
            ", , false"                     // Invalid: both null
    })
    void testSearchRestaurants(String nameInput, Integer categoryInput, boolean expectSuccess) throws IOException, InvalidCategoryException {
        clearDatabase();
        MultipartFile picture = new MockMultipartFile(
                "picture",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        // Prepare request
        AddRestaurantRequest request = new AddRestaurantRequest();
        request.setRestaurantName("My Test Restaurant");
        request.setRestaurantCategory(1);

        if (expectSuccess) {
            for (int i = 1; i < 3; i++) {
                Restaurant restaurant = restaurantService.addRestaurant(request, picture);
                assertNotNull(restaurant);

                List<Restaurant> searchResult = restaurantService.searchRestaurants(nameInput, categoryInput);

                assertEquals(i, searchResult.size());
            }
        } else {
            assertThrows(Exception.class, () -> restaurantService.searchRestaurants(nameInput, categoryInput));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Good food, 1, true",
            "Good food, 5, true",
            "Good food, -1, false",
            "Good food, 6, false"
    })
    void testAddReview(String review, Integer rating, boolean expectSuccess) throws Exception {
        Restaurant restaurant = new Restaurant("Test Name", 1);
        restaurant = restaurantRepository.save(restaurant);

        AddReviewRequest request = new AddReviewRequest();
        request.setRestaurantId(restaurant.getRestaurantId());
        request.setReview(review);
        request.setRating(rating);

        if (expectSuccess) {
            boolean result = restaurantService.addReview(request);
            assertTrue(result);
        } else {
            assertThrows(Exception.class, () -> restaurantService.addReview(request));
        }

        request.setRestaurantId(100);
        assertThrows(Exception.class, () -> restaurantService.addReview(request));
    }

    @Test
    void testCalculateAverageRate() throws RestaurantNotFoundException, NoRatingException {
        Restaurant restaurant = new Restaurant("Test Name", 1);
        restaurant = restaurantRepository.save(restaurant);

        double ratingSum = 0;
        int iteration = (int) (Math.random() * 5) + 1;
        for (int i = 0; i < iteration; i++) {
            AddReviewRequest request = new AddReviewRequest();
            request.setRestaurantId(restaurant.getRestaurantId());
            request.setReview("Sample Review");
            request.setRating(i);
            restaurantService.addReview(request);
            ratingSum += i;
        }

        double expectedAverageRate = (ratingSum / iteration);
        double averageRate = restaurantService.calculateAverageRating(restaurant.getRestaurantId());
        assertEquals(expectedAverageRate, averageRate);
    }


    private void clearDatabase() {
        reviewRepository.deleteAll();
        restaurantRepository.deleteAll();
    }
}
