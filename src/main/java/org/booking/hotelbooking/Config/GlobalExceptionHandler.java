package org.booking.hotelbooking.Config;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обробка всіх неспійманих винятків
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Сталася помилка: " + e.getMessage());
        return "redirect:/";
    }

    // Обробка 404 (неправильний URL)
    @RequestMapping("/error")
    public String handle404Error(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Сторінку не знайдено");
        return "redirect:/";
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Доступ заборонено");
        return "redirect:/";
    }

}
