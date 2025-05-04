package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class UserRegisterController {

    private final UserService userService;

    @Autowired
    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String register() {
        return "register";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") User user,
                               Model model,
                               @RequestParam("fullPhone") String fullPhone) {
        try {
            user.setPhone(fullPhone);
            userService.registerNewUser(user);
            return "redirect:/login?registered=true";
        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }
}
