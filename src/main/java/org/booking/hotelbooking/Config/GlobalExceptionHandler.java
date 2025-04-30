package org.booking.hotelbooking.Config;

import org.booking.hotelbooking.Service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private  Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Обробка всіх неспійманих винятків

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        logger.error("Global error: {}", e.getMessage(), e); // Додано логування
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
        logger.warn("Спроба доступу без належних прав");
        redirectAttributes.addFlashAttribute("error", "Доступ заборонено");
        return "redirect:/";
    }







}
