package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class UserLoginController {

    @GetMapping
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Невірний email або пароль");
        }
        if (logout != null) {
            model.addAttribute("message", "Ви успішно вийшли з системи");
        }
        return "login";
    }
}
