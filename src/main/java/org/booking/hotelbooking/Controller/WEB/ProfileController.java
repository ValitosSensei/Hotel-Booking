package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Entity.Role;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Repository.HotelRepository;
import org.booking.hotelbooking.Service.HotelService;
import org.booking.hotelbooking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final HotelService hotelService;
    private final HotelRepository hotelRepository;

    @Autowired
    public ProfileController(UserService userService,
                             HotelService hotelService, HotelRepository hotelRepository) {
        this.userService = userService;
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
    }

    @GetMapping
    public String showProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
                              Model model) {
        if (securityUser == null) {
            return "redirect:/login";
        }

        try {
            User fullUser = userService.getUserByEmail(securityUser.getUsername());
            model.addAttribute("user", fullUser);

            boolean isManager = fullUser.getRoles().contains(Role.ROLE_MANAGER);
            model.addAttribute("isManager", isManager);

            if (isManager) {
                List<Hotel> userHotels = hotelService.getHotelsByManagerId(fullUser.getId());
                model.addAttribute("userHotels", userHotels);
            }

            return "profile";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/request-manager")
    public String requestManagerRole(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
            @RequestParam String hotelName,
            @RequestParam String hotelAddress,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = userService.getUserByEmail(securityUser.getUsername());
            userService.requestManagerRole(user.getId(), hotelName, hotelAddress); // Оновлений метод
            redirectAttributes.addFlashAttribute("message", "Запит відправлено! Очікуйте підтвердження.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }
}