package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.Entity.Booking;
import org.booking.hotelbooking.Entity.BookingStatus;
import org.booking.hotelbooking.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingScheduler {

    private BookingService bookingService;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingScheduler(BookingService bookingService,
                            BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredBookings() {
        List<Booking> pendingBookings = bookingService.getPendingBookings();
        for (Booking booking : pendingBookings) {
            if (booking.getCreatedAt() == null) {

                continue;
            }

            long minutesPassed = ChronoUnit.MINUTES.between(
                    booking.getCreatedAt(),
                    LocalDateTime.now()
            );

            if (minutesPassed > 30) {
                bookingService.cancelBooking(booking.getId());

            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Щодня о півночі
    public void markCompletedBookings() {
        List<Booking> bookings = bookingRepository.findByStatusIn(
                List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );

        LocalDate today = LocalDate.now();
        bookings.forEach(booking -> {
            if (booking.getCheckOutDate().isBefore(today)) {
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(booking);
            }
        });
    }
}
