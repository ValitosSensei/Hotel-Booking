package org.booking.hotelbooking.Repository;


import org.booking.hotelbooking.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
