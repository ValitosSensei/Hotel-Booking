package org.booking.hotelbooking.Repository;


import org.booking.hotelbooking.Entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {


    @EntityGraph(attributePaths = {"hotel"})
    Optional<Room> findById(Long id);


    List<Room> findByHotelId(Long hotelId);
    List<Room> findByHotelIdAndAvailable(Long hotelId, boolean available);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
            "AND EXISTS (" +
            "   SELECT b FROM Booking b WHERE b.room = r " +
            "   AND (:checkOut > b.checkInDate AND :checkIn < b.checkOutDate)" +
            ")")
    List<Room> findOccupiedRoomsByDates(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
            "AND r.available = true " + // Додано перевірку на загальну доступність
            "AND NOT EXISTS (" +
            "   SELECT b FROM Booking b WHERE b.room = r " +
            "   AND (:checkOut > b.checkInDate AND :checkIn < b.checkOutDate)" +
            ")")
    List<Room> findAvailableRoomsByDates(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Query("SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND (" +
            "   (b.checkInDate <= :checkIn AND b.checkOutDate >= :checkOut) OR " +
            "   (b.checkInDate BETWEEN :checkIn AND :checkOut) OR " +
            "   (b.checkOutDate BETWEEN :checkIn AND :checkOut)" +
            ")")
    boolean isRoomAvailable(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

}
