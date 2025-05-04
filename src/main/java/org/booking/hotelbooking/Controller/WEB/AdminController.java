package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.DTO.RoomDTO;
import org.booking.hotelbooking.Entity.*;
import org.booking.hotelbooking.Repository.RoleRequestRepository;
import org.booking.hotelbooking.Service.EmailService;
import org.booking.hotelbooking.Service.HotelService;
import org.booking.hotelbooking.Service.RoomService;
import org.booking.hotelbooking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final HotelService hotelService;
    private final UserService userService;
    private final RoomService roomService;
    private final RoleRequestRepository roleRequestRepository;

    @Autowired
    private EmailService emailService;


    @Autowired
    public AdminController(HotelService hotelService,
                           UserService userService,
                           RoomService roomService, RoleRequestRepository roleRequestRepository) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.roomService = roomService;
        this.roleRequestRepository = roleRequestRepository;
    }

    @GetMapping
    public String adminPanel(Model model,
     @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {

        List<Hotel> hotels = hotelService.listHotel();
        List<Room> rooms = roomService.getAllRooms();
        List<RoleRequest> requests = roleRequestRepository.findByIsApprovedFalseAndRejectedFalse();

        model.addAttribute("hotels", hotels);
        model.addAttribute("rooms", rooms);
        model.addAttribute("requests", requests); // Додано
        model.addAttribute("searchedUsers", Collections.emptyList());

        return "adminPanel";
    }

//    ============Hotel=============

    @GetMapping("/editHotel/{hotelId}")
    public String showEditHotel(@PathVariable Long hotelId, Model model) {
        Hotel hotel = hotelService.getHotelById(hotelId);
        model.addAttribute("hotel", hotel);
        return "edit-hotel";
    }

    @PostMapping("/editHotel/{hotelId}")
    public String editHotel(@PathVariable Long hotelId,
                            @ModelAttribute Hotel hotel,
                            RedirectAttributes redirectAttributes) {
        hotelService.UpdateHotel(hotelId, hotel);
        redirectAttributes.addFlashAttribute("success", "Hotel updated successfully");
        return "redirect:/admin";
    }

    @GetMapping("/deleteHotel/{hotelId}")
    public String deleteHotel(@PathVariable Long hotelId, RedirectAttributes redirectAttributes) {
        hotelService.deleteHotel(hotelId);
        redirectAttributes.addFlashAttribute("success", "Hotel deleted successfully");
        return "redirect:/admin";
    }
//    =============Room=====================

    @GetMapping("/editRoom/{roomId}")
    public String showEditRoomForm(@PathVariable Long roomId, Model model) {
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("room", room);
        return "edit-room";  // Форма для редагування кімнати
    }

    @PostMapping("/editRoom/{roomId}")
    public String editRoom(@PathVariable Long roomId, @ModelAttribute Room roomData, RedirectAttributes redirectAttributes) {
        Room room = roomService.getRoomById(roomId);

        room.setRoomNumber(roomData.getRoomNumber());
        room.setType(roomData.getType());
        room.setPrice(roomData.getPrice());
        room.setAvailableForDates(roomData.isAvailableForDates());

        room.setHotel(room.getHotel());

        roomService.saveRoom(room);  // Оновлення кімнати
        redirectAttributes.addFlashAttribute("success", "Кімнату оновлено");
        return "redirect:/admin";
    }

    @GetMapping("/deleteRoom/{roomId}")
    public String deleteRoom(@PathVariable Long roomId, RedirectAttributes redirectAttributes) {
        roomService.deleteRoom(roomId);  // Видалення кімнати
        redirectAttributes.addFlashAttribute("success", "Кімнату видалено");
        return "redirect:/admin";
    }

//    ================ UserManager==================
@PostMapping("/approve-request/{id}")
public String approvedRequest(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes
) {
    RoleRequest request = roleRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Запит не знайдено"));

    User user = request.getUser();
    user.getRoles().add(Role.ROLE_MANAGER); // Додати роль
    userService.saveUser(user); // Зберегти оновлення

    userService.approveRequest(id);
    // Отримати дані користувача та готелю

    String hotelName = request.getHotelName();

    // Підтвердити запит
    userService.approveRequest(id);

    // Надіслати лист
    try {
        emailService.sendRequestApprovedEmail(user.getEmail(), hotelName);
        redirectAttributes.addFlashAttribute("success", "Роль менеджера надано та лист відправлено");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Помилка відправки листа: " + e.getMessage());
    }

    return "redirect:/admin";
}


    @GetMapping("/searchUser")
    public String searchUser(@RequestParam String email, Model model) {
        try {
            User user = userService.getUserByEmail(email);
            model.addAttribute("searchedUsers", Collections.singletonList(user));
        } catch (RuntimeException e) {
            model.addAttribute("searchedUsers", Collections.emptyList());
        }
        return "adminPanel :: #userResults"; // Повертаємо лише таблицю
    }

    @PostMapping("/grant-admin/{userId}")
    public String grantAdmin(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(userId);
        user.getRoles().add(Role.ROLE_ADMIN);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Admin role granted");
        return "redirect:/admin";
    }

    @PostMapping("/reject-request/{id}")
    public String rejectRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String comment, // Додано параметр
            RedirectAttributes redirectAttributes
    ) {
        userService.rejectRequest(id, comment); // Передаємо comment
        redirectAttributes.addFlashAttribute("success", "Запит відхилено");
        return "redirect:/admin";
    }

}
