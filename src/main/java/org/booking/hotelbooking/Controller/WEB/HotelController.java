package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.DTO.CreateHotelWithRoomsDTO;
import org.booking.hotelbooking.DTO.CreateRoomDTO;
import org.booking.hotelbooking.DTO.HotelWithRoomsDTO;
import org.booking.hotelbooking.DTO.RoomDTO;
import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Entity.Role;
import org.booking.hotelbooking.Entity.Room;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Entity.Review;
import org.booking.hotelbooking.Service.ReviewService;
import org.booking.hotelbooking.Service.HotelService;
import org.booking.hotelbooking.Service.RoomService;
import org.booking.hotelbooking.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping
public class HotelController {

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);
    private final HotelService hotelService;
    private final RoomService roomService;
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public HotelController(HotelService hotelService,
                           RoomService roomService,
                           UserService userService,
                           ReviewService reviewService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "maxRating", required = false) Double maxRating,
            Model model
    ) {
        // Якщо вибрано "Усі міста", очищуємо фільтри
        if (city == null || city.isEmpty()) {
            minRating = null;
            maxRating = null;
        }

        // Отримуємо готелі, відсортовані за рейтингом та фільтруємо по рейтингу
        List<Hotel> hotels = hotelService.getHotelsByCityAndSortByRating(city, sortOrder, minRating, maxRating);

        // Отримуємо список міст
        List<String> cities = hotelService.getAllCities();

        // Додаємо атрибути в модель
        model.addAttribute("hotels", hotels);
        model.addAttribute("cities", cities);
        model.addAttribute("selectedCity", city);
        model.addAttribute("sortOrder", sortOrder); // Для вибору порядку сортування
        model.addAttribute("minRating", minRating); // Для мінімального рейтингу
        model.addAttribute("maxRating", maxRating); // Для максимального рейтингу
        return "index";
    }

    @GetMapping("/{hotelId}/rooms")
    @Transactional
    public String getRooms(
            @PathVariable Long hotelId,
            @RequestParam(name = "filter", defaultValue = "all") String filter,
            @RequestParam(name = "checkIn", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model
    ) {
        try {
            // Валідація дат
            if (checkIn != null && checkOut != null && checkIn.isAfter(checkOut)) {
                throw new IllegalArgumentException("Дата заїзду повинна бути раніше дати виїзду");
            }

            List<RoomDTO> rooms = roomService.getRoomsInHotelWithFilter(hotelId, filter, checkIn, checkOut);
            model.addAttribute("rooms", rooms);
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("checkIn", checkIn);
            model.addAttribute("checkOut", checkOut);
            model.addAttribute("currentFilter", filter);

            Hotel hotel = hotelService.getHotelById(hotelId);
            List<Review> reviews = reviewService.getReviewsByHotel(hotel);
            model.addAttribute("reviews", reviews);

            return "rooms";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
    }
//    @GetMapping("/create")
//    public String showCreateForm(Model model) {
//        CreateHotelWithRoomsDTO dto = new CreateHotelWithRoomsDTO();
//
//        dto.getRooms().add(new CreateRoomDTO());
//        model.addAttribute("hotelDTO", dto);
//        return "create-hotel";
//    }
//
//    @PostMapping("/create")
//    public String createHotel(
//            @ModelAttribute("hotelDTO") CreateHotelWithRoomsDTO hotelDTO,
//            BindingResult result,
//            RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            return "create-hotel";
//        }
//
//        try {
//            // Отримуємо користувача за ID
//            User user = userService.getUserById(hotelDTO.getUserId());
//
//            // Перевіряємо ролі
//            Set<Role> userRoles = user.getRoles();
//            boolean isAllowed = userRoles.contains(Role.ROLE_ADMIN) || userRoles.contains(Role.ROLE_MANAGER);
//
//            if (!isAllowed) {
//                redirectAttributes.addFlashAttribute("error", "Тільки адміністратори та менеджери можуть створювати готелі");
//                return "redirect:/create";
//            }
//
//            // Якщо перевірка успішна - створюємо готель
//            hotelService.createHotelWithRooms(hotelDTO);
//            redirectAttributes.addFlashAttribute("success", "Готель створено");
//        } catch (RuntimeException ex) {
//            redirectAttributes.addFlashAttribute("error", ex.getMessage());
//            return "redirect:/create";
//        }
//
//        return "redirect:/";
//    }

    @PostMapping("/{hotelId}/reviews")
    public String submitReview(
            @PathVariable Long hotelId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            Principal principal, // Для отримання поточного користувача
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
//            redirectAttributes.addFlashAttribute("error", "Ви повинні бути авторизовані для того, щоб залишити відгук.");
            return "redirect:/{hotelId}/rooms";
        }

        User user = userService.getUserByEmail(principal.getName());
        Hotel hotel = hotelService.getHotelById(hotelId);

        // Перевірка, чи користувач має бронювання в цьому готелі
        // if (!bookingService.hasUserBookedHotel(user, hotel)) {
        //     redirectAttributes.addFlashAttribute("error", "Ви не можете залишити відгук без бронювання");
        //     return "redirect:/{hotelId}/rooms";
        // }

        reviewService.createOrUpdateReview(user, hotel, rating, comment);

        return "redirect:/{hotelId}/rooms";
    }



}
