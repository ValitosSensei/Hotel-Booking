package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.DTO.BookingRequest;
import org.booking.hotelbooking.Entity.Booking;
import org.booking.hotelbooking.Entity.BookingStatus;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final RoomService roomService;
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final RoomRepository roomRepository;
    private final EmailService emailService;


    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserService userService,
                          RoomService roomService,
                          RoomRepository roomRepository,
                          EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.emailService = emailService;
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

        // Замінити цей рядок:
        // if (!roomService.isRoomAvailable(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate())) {

        // На цей:
        if (!isRoomAvailable(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate())) {
            throw new RuntimeException("Кімната вже зайнята на ці дати");
        }

        // Прив'язуємо завантажену кімнату до бронювання
        booking.setRoom(room);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        roomRepository.save(room);

        String token = UUID.randomUUID().toString();
        booking.setConfirmationToken(token);

        // Відправляємо email
        String confirmationLink = "http://localhost:8080/bookings/confirm?token=" + token;
        emailService.sendConfirmationEmail(booking.getUser().getEmail(), confirmationLink);

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронювання не знайдено"));

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Не можна скасувати завершене бронювання");
        }

        Room room = booking.getRoom();

        roomRepository.save(room);

        bookingRepository.delete(booking);
    }


    @Transactional
    public void requestTransfer(Long bookingId, String newUserEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронювання не знайдено"));

        User newUser = userService.getUserByEmail(newUserEmail);



        // Генерація токену та збереження часу
        String transferToken = UUID.randomUUID().toString();
        booking.setTransferToken(transferToken);
        booking.setTransferRequestTime(LocalDateTime.now());
        booking.setTransferredTo(newUser); // Встановлюємо майбутнього власника

        bookingRepository.save(booking);

        // Відправка email
        String confirmationLink = "http://ваш-сайт/bookings/transfer/confirm?token=" + transferToken;
        emailService.sendTransferConfirmationEmail(newUser.getEmail(), confirmationLink);
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

    @Transactional
    public void confirmTransfer(String token) {
        Booking booking = bookingRepository.findByTransferToken(token)
                .orElseThrow(() -> new RuntimeException("Невірний токен"));

        // Перевірка часу
        long hoursPassed = ChronoUnit.HOURS.between(booking.getTransferRequestTime(), LocalDateTime.now());
        if (hoursPassed > 24) {
            throw new RuntimeException("Час на підтвердження передачі минув");
        }

        User oldUser = booking.getUser();
        User newUser = booking.getTransferredTo();

        // Оновлення зв'язків
        oldUser.getBookings().remove(booking);
        newUser.getBookings().add(booking);
        booking.setUser(newUser);
        booking.setTransferredFrom(oldUser);
        booking.setTransferToken(null);

        userService.saveUser(oldUser);
        userService.saveUser(newUser);
        bookingRepository.save(booking);
    }
    @Transactional
    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронювання не знайдено"));
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }



    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatus(BookingStatus.PENDING);
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Booking> overlappingBookings = bookingRepository.findByRoomIdAndStatus(
                roomId,
                BookingStatus.CONFIRMED,
                checkInDate,
                checkOutDate
        );
        return overlappingBookings.isEmpty();
    }

    public Booking findByConfirmationToken(String token) {
        return bookingRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Невірний токен"));
    }

    public List<Booking> getPendingTransfers() {
        List<Booking> allTransfers = bookingRepository.findByTransferredToIsNotNull();

        return allTransfers.stream()
                .filter(booking -> {
                    LocalDateTime requestTime = booking.getTransferRequestTime();
                    if (requestTime == null) return false;

                    // Перевірка часу: якщо з моменту запиту пройшло менше 24 годин
                    long hoursPassed = ChronoUnit.HOURS.between(requestTime, LocalDateTime.now());
                    return hoursPassed <= 24;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Booking createBookingFromRequest(BookingRequest request, User user) {
        Room room = roomService.getRoomById(request.getRoomId());

        LocalDate currentDate = LocalDate.now();
        if (request.getCheckInDate().isBefore(currentDate)) {
            throw new RuntimeException("Дата заїзду не може бути в минулому");
        }
        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new RuntimeException("Дата виїзду має бути після дати заїзду");
        }

        if (!isRoomAvailable(room.getId(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new RuntimeException("Кімната вже зайнята");
        }


        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setConfirmationToken(UUID.randomUUID().toString());
        booking.setCreatedAt(LocalDateTime.now());

        // Генерація посилання для підтвердження
        String confirmationLink = "http://localhost:8080/bookings/confirm?token=" + booking.getConfirmationToken();

        // Відправка листа
        emailService.sendConfirmationEmail(user.getEmail(), confirmationLink);

        return bookingRepository.save(booking);
    }


}
