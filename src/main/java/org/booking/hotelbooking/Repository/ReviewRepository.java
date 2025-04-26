package org.booking.hotelbooking.Repository;


import org.booking.hotelbooking.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
