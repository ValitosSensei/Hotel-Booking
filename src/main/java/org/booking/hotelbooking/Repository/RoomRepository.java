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



    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
            "AND EXISTS (" +
            "   SELECT b FROM Booking b WHERE b.room = r " +
            "   AND b.status = 'CONFIRMED' " +
            "   AND (b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)" +
            ")")
    List<Room> findOccupiedRoomsByDates(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
            "AND NOT EXISTS (" +
            "   SELECT b FROM Booking b WHERE b.room = r " +
            "   AND b.status = 'CONFIRMED' " +
            "   AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)" + // Змінено умову
            ")")
    List<Room> findAvailableRoomsByDates(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Query("SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.status = 'CONFIRMED' " +
            "AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)") // Змінено умову
    boolean isRoomAvailable(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

}
