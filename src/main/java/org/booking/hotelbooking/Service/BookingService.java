package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.Entity.Booking;
import org.booking.hotelbooking.Entity.Room;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Repository.BookingRepository;
import org.booking.hotelbooking.Repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final RoomService roomService;
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final RoomRepository roomRepository;


    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserService userService, RoomService roomService, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.roomService = roomService;
        this.roomRepository = roomRepository;
    }
    @Transactional
    public Booking createBooking(Booking booking) {
        if (booking.getUser() == null) {
            throw new RuntimeException("Користувач не знайдений");
        }

        // Підтягуємо повну кімнату з готелем
        Room room = roomService.getRoomById(booking.getRoom().getId());
        if (room == null) {
            throw new RuntimeException("Кімната не знайдена");
        }

        if (!roomService.isRoomAvailable(room.getId(), booking.getCheckInDate(), booking.getCheckOutDate())) {
            throw new RuntimeException("Кімната вже зайнята на ці дати");
        }

        // Прив'язуємо завантажену кімнату до бронювання
        booking.setRoom(room);

        room.setAvailable(false);
        roomRepository.save(room);

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронювання не знайдено"));

        Room room = booking.getRoom();
        room.setAvailable(true); // Відновлюємо статус "вільна"
        roomRepository.save(room);

        bookingRepository.delete(booking);
    }


    @Transactional
    public void transferBooking(Long bookingId, Long newUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронювання не знайдено"));

        // Видаляємо перевірку на available
        User currentUser = booking.getUser();
        User newUser = userService.getUserById(newUserId);

        booking.setTransferredFrom(currentUser);
        booking.setTransferredTo(newUser);

        currentUser.getBookings().remove(booking);
        newUser.getBookings().add(booking);
        booking.setUser(newUser);

        bookingRepository.save(booking);
        userService.saveUser(currentUser);
        userService.saveUser(newUser);
    }


}
