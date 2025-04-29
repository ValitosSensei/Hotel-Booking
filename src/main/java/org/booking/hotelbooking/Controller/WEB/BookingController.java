package org.booking.hotelbooking.Controller.WEB;


import org.booking.hotelbooking.DTO.BookingRequest;
import org.booking.hotelbooking.Entity.*;
import org.booking.hotelbooking.Service.BookingService;
import org.booking.hotelbooking.Service.RoomService;
import org.booking.hotelbooking.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;


@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);


    @Autowired
    public BookingController(BookingService bookingService,
                             RoomService roomService,
                             UserService userService
                             ) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.userService = userService;

    }


    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails securityUser // securityUser буде null, якщо користувач не авторизований
    ) {
        try {
            logger.info("Спроба бронювання: {}", request);

            // Перевірка авторизації
            if (securityUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "Будь ласка, увійдіть в систему"
                ));
            }

            User user = userService.getUserByEmail(securityUser.getUsername());
            Booking booking = bookingService.createBookingFromRequest(request, user);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Бронювання створено! Підтвердьте через email."
            ));
        } catch (RuntimeException ex) {
            logger.error("Помилка бронювання: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", ex.getMessage()
            ));
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
            logger.info("Отримано токен підтвердження: {}", token);
            Booking booking = bookingService.findByConfirmationToken(token);
            bookingService.confirmBooking(booking.getId());
            redirectAttributes.addFlashAttribute("success", "Бронювання підтверджено!");
        } catch (RuntimeException ex) {
            logger.error("Помилка підтвердження: {}", ex.getMessage());
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
