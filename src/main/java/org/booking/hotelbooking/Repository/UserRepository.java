package org.booking.hotelbooking.Repository;



import org.booking.hotelbooking.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.bookings WHERE u.id = :userId")
    Optional<User> findByIdWithBookings(@Param("userId") Long userId);
}
