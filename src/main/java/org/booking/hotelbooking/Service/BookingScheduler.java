package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.Entity.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingScheduler {

    private BookingService bookingService;

    @Autowired
    public BookingScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 60000) // Перевірка кожну хвилину
    public void cancelExpiredBookings() {
        List<Booking> pendingBookings = bookingService.getPendingBookings();
        for (Booking booking : pendingBookings) {
            long minutesPassed = ChronoUnit.MINUTES.between(booking.getCreatedAt(), LocalDateTime.now());
            if (minutesPassed > 30) { // 30 хвилин на підтвердження
                bookingService.cancelBooking(booking.getId());
            }
        }
    }
}
