package org.booking.hotelbooking.Repository;


import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Entity.Review;
import org.booking.hotelbooking.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Знаходити відгук тільки для реальних юзерів, ігноруючи бота (ID 1)

    Optional<Review> findByUserAndHotel(User user, Hotel hotel);
    List<Review> findByHotel(Hotel hotel);
}
