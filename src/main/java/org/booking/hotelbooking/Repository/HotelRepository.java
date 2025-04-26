package org.booking.hotelbooking.Repository;


import org.booking.hotelbooking.Entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    // Додаємо метод для отримання унікальних міст
    @Query("SELECT DISTINCT h.city FROM Hotel h")
    List<String> findDistinctCities();


    // Додаємо метод для фільтрації за містом
    List<Hotel> findByCity(String city);

    List<Hotel> findByOwnerId(Long ownerId); // Знайти готелі за ID власника (менеджера)
    Optional<Hotel> findByIdAndOwnerId(Long id, Long ownerId); // Знайти готель за ID та власник


}
