package com.restful.restaurant.api.service;

import com.restful.restaurant.api.dto.AddRestaurantRequest;
import com.restful.restaurant.api.dto.AddReviewRequest;
import com.restful.restaurant.api.exception.InvalidCategoryException;
import com.restful.restaurant.api.exception.NoRatingException;
import com.restful.restaurant.api.exception.RestaurantNotFoundException;
import com.restful.restaurant.api.model.Restaurant;
import com.restful.restaurant.api.model.Review;
import com.restful.restaurant.api.repository.RestaurantRepository;
import com.restful.restaurant.api.repository.ReviewRepository;
import com.restful.restaurant.api.repository.specification.RestaurantSpecifications;
import com.restful.restaurant.enums.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RestaurantService {

    private final Path imageSavePath;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public RestaurantService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.imageSavePath = Files.createDirectories(Paths.get(uploadDir).toAbsolutePath().normalize());
    }


    public Restaurant addRestaurant(AddRestaurantRequest request, MultipartFile picture) throws IOException, InvalidCategoryException {
        // Validation
        if (request.getRestaurantName() == null || request.getRestaurantName().isEmpty())
            throw new IllegalArgumentException("Restaurant name cannot be empty");
        if (!Category.isValidCode(request.getRestaurantCategory()))
            throw new InvalidCategoryException();
        if (!isImage(picture))
            throw new IllegalArgumentException("Invalid file, only images can be uploaded");

        Restaurant restaurant = new Restaurant(request.getRestaurantName(), request.getRestaurantCategory());

        String imageSavedPath = saveFile(picture).toString();
        restaurant.setRestaurantPictureUrl(imageSavedPath);
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> searchRestaurants(String name, Integer category) throws InvalidCategoryException {
        if ((name == null || name.isEmpty()) && category == null)
            throw new IllegalArgumentException("Request must contain at least one parameter");
        if (category != null && !Category.isValidCode(category))
            throw new InvalidCategoryException();

        Specification<Restaurant> spec = (root, query, cb) -> cb.conjunction(); // a no-op starting spec

        spec = spec.and(RestaurantSpecifications.hasNameLike(name))
                .and(RestaurantSpecifications.hasCategory(category));
        return restaurantRepository.findAll(spec);
    }

    public boolean addReview(AddReviewRequest request) throws RestaurantNotFoundException {

        if (!restaurantRepository.existsById(request.getRestaurantId()))
            throw new RestaurantNotFoundException();
        if (request.getReview() == null || request.getReview().isEmpty())
            throw new IllegalArgumentException("Review is required");
        if (request.getRating() < 0 || request.getRating() > 5)
            throw new IllegalArgumentException("Rating must be between 0 and 5");

        Review review = new Review(request.getReview(), request.getRating(), new Restaurant(request.getRestaurantId()));
        reviewRepository.save(review);
        return true;
    }

    public double calculateAverageRating(Long restaurantId) throws RestaurantNotFoundException, NoRatingException {
        if (!restaurantRepository.existsById(restaurantId))
            throw new RestaurantNotFoundException();

        List<Review> reviews = reviewRepository.findByRestaurant_RestaurantId(restaurantId);

        if (reviews.isEmpty())
            throw new NoRatingException();

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private Path saveFile(MultipartFile file) throws IOException {
        if (!Files.exists(imageSavePath)) {
            Files.createDirectories(imageSavePath);
        }
        Path filePath = imageSavePath.resolve(file.getOriginalFilename());
        file.transferTo(filePath.toFile());
        return filePath;
    }
}
