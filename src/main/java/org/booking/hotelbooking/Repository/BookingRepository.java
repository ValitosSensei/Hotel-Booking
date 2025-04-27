package org.booking.hotelbooking.Repository;


import org.booking.hotelbooking.Entity.Booking;
import org.booking.hotelbooking.Entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(BookingStatus status);
    @Query("SELECT b FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.status = :status " +
            "AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    List<Booking> findByRoomIdAndStatus(
            @Param("roomId") Long roomId,
            @Param("status") BookingStatus status,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
