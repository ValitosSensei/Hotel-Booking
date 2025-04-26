package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.Entity.Review;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review createOrUpdateReview(User user, Hotel hotel, Integer rating, String comment) {
        Optional<Review> existingReview = reviewRepository.findByUserAndHotel(user, hotel);
        Review review;

        if (existingReview.isPresent()) {
            review = existingReview.get();
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedDate(LocalDateTime.now());
        } else {
            review = new Review();
            review.setUser(user);
            review.setHotel(hotel);
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedDate(LocalDateTime.now());
        }

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByHotel(Hotel hotel) {
        return reviewRepository.findByHotel(hotel);
    }

    public Optional<Review> getUserReviewForHotel(User user, Hotel hotel) {
        return reviewRepository.findByUserAndHotel(user, hotel);
    }
}