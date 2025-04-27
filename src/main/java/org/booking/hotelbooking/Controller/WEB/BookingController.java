package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.Entity.*;
import org.booking.hotelbooking.Service.BookingService;
import org.booking.hotelbooking.Service.RoomService;
import org.booking.hotelbooking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final UserService userService;

    @Autowired
    public BookingController(BookingService bookingService,
                             RoomService roomService,
                             UserService userService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping("/rooms/{roomId}/book")
    public String showBookingForm(@PathVariable Long roomId, Model model) {
        Room room = roomService.getRoomById(roomId); // Завантажує Room з Hotel через @EntityGraph
        if (room == null) {
            throw new RuntimeException("Кімната не знайдена");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        model.addAttribute("booking", booking);
        return "booking-form";
    }

    @PostMapping("/create")
    public String createBooking(
            @ModelAttribute("booking") Booking booking,
            BindingResult result,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
            RedirectAttributes redirectAttributes
    ) {
        if (securityUser == null) {
            redirectAttributes.addFlashAttribute("error", "Будь ласка, увійдіть в систему");
            return "redirect:/login";
        }

        try {
            // Отримати повну сутність User з бази
            User user = userService.getUserByEmail(securityUser.getUsername());
            booking.setUser(user); // Прив'язати автентифікованого користувача

            // Валідація дат
            if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
                redirectAttributes.addFlashAttribute("error", "Дата виїзду має бути після дати заїзду");
                return "redirect:/bookings/rooms/" + booking.getRoom().getId() + "/book";
            }

            bookingService.createBooking(booking);
            return "redirect:/bookings/success";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/bookings/rooms/" + booking.getRoom().getId() + "/book";
        }
    }

    @GetMapping("/success")
    public String showBookingSuccess() {
        return "booking-success";
    }

    @GetMapping("/{bookingId}/transfer")
    public String showTransferForm(@PathVariable Long bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        return "transfer-form";
    }

    @PostMapping("/{bookingId}/transfer")
    public String transferBooking(
            @PathVariable Long bookingId,
            @RequestParam("newUserId") Long newUserId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookingService.transferBooking(bookingId, newUserId);
            redirectAttributes.addFlashAttribute("success", "Бронювання успішно передано");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("success", "Бронювання скасовано");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }

    // Підтвердження через токен (email)
    @GetMapping("/confirm")
    public String confirmBookingByToken(
            @RequestParam String token,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Використовуйте bookingService замість bookingRepository
            Booking booking = bookingService.findByConfirmationToken(token);
            bookingService.confirmBooking(booking.getId());
            redirectAttributes.addFlashAttribute("success", "Бронювання підтверджено через email!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }

    // Підтвердження через кнопку (для тестування)
    @PostMapping("/{bookingId}/confirm")
    public String confirmBookingByButton(
            @PathVariable Long bookingId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookingService.confirmBooking(bookingId);
            redirectAttributes.addFlashAttribute("success", "Бронювання підтверджено через кнопку!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/{bookingId}/request-transfer")
    public String requestTransfer(
            @PathVariable Long bookingId,
            @RequestParam("newUserEmail") String newUserEmail,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookingService.requestTransfer(bookingId, newUserEmail);
            redirectAttributes.addFlashAttribute("success", "Запит на передачу відправлено");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/transfer/confirm")
    public String confirmTransfer(
            @RequestParam String token,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookingService.confirmTransfer(token);
            redirectAttributes.addFlashAttribute("success", "Передачу підтверджено");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }
}
