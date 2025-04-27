package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.Entity.Booking;
import org.booking.hotelbooking.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TransferScheduler {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @Autowired
    public TransferScheduler(BookingService bookingService,
                             BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(fixedRate = 3600000) // Перевірка кожну годину
    public void cancelExpiredTransfers() {
        List<Booking> pendingTransfers = bookingService.getPendingTransfers();
        for (Booking booking : pendingTransfers) {
            long hoursPassed = ChronoUnit.HOURS.between(
                    booking.getTransferRequestTime(),
                    LocalDateTime.now()
            );
            if (hoursPassed > 24) {
                booking.setTransferToken(null);
                booking.setTransferredTo(null);
                bookingRepository.save(booking);
            }
        }
    }
}
