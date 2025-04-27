package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.DTO.CreateHotelWithRoomsDTO;
import org.booking.hotelbooking.DTO.CreateRoomDTO;
import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Entity.Role;
import org.booking.hotelbooking.Entity.Room;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Service.HotelService;
import org.booking.hotelbooking.Service.RoomService;
import org.booking.hotelbooking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final HotelService hotelService;
    private final UserService userService;
    private final RoomService roomService;

    @Autowired
    public ManagerController(HotelService hotelService, UserService userService, RoomService roomService) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping("/hotels/{hotelId}")
    public String manageHotel(
            @PathVariable Long hotelId,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        User user = userService.getUserById(userId);
        if (!user.getRoles().contains(Role.ROLE_MANAGER)) {
            return "redirect:/profile?error=Доступ заборонено";
        }

        Hotel hotel = hotelService.getHotelByIdAndOwnerId(hotelId, userId);
        model.addAttribute("hotel", hotel);
        model.addAttribute("userId", userId);
        return "manage-hotel";
    }

    @GetMapping("/editHotel/{hotelId}")
    public String showEditHotelForm(
            @PathVariable Long hotelId,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        Hotel hotel = hotelService.getHotelByIdAndOwnerId(hotelId, userId);
        model.addAttribute("hotel", hotel);
        model.addAttribute("userId", userId);
        return "manager-edit-hotel";
    }

    @PostMapping("/editHotel/{hotelId}")
    public String editHotel(
            @PathVariable Long hotelId,
            @RequestParam("userId") Long userId,
            @ModelAttribute("hotel") Hotel hotelData,
            RedirectAttributes redirectAttributes
    ) {
        Hotel existingHotel = hotelService.getHotelByIdAndOwnerId(hotelId, userId);
        existingHotel.setName(hotelData.getName());
        existingHotel.setAddress(hotelData.getAddress());
        existingHotel.setCity(hotelData.getCity());
        existingHotel.setCountry(hotelData.getCountry());
        existingHotel.setContactInfo(hotelData.getContactInfo());

        hotelService.UpdateHotel(hotelId,existingHotel);
        redirectAttributes.addAttribute("userId", userId);
        return "redirect:/manager/hotels/{hotelId}";
    }

    @GetMapping("/deleteHotel/{hotelId}")
    public String deleteHotel(
            @PathVariable Long hotelId,
            @RequestParam("userId") Long userId,
            RedirectAttributes redirectAttributes
    ) {
        hotelService.deleteHotel(hotelId);
        redirectAttributes.addAttribute("userId", userId);
        return "redirect:/profile";
    }

    @GetMapping("/editRoom/{roomId}")
    public String showEditRoomForm(
            @PathVariable Long roomId,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("room", room);
        model.addAttribute("userId", userId);
        return "manager-edit-room";
    }

    @PostMapping("/editRoom/{roomId}")
    public String editRoom(
            @PathVariable Long roomId,
            @RequestParam("userId") Long userId,
            @ModelAttribute("room") Room roomData,
            RedirectAttributes redirectAttributes
    ) {
        Room room = roomService.getRoomById(roomId);
        room.setRoomNumber(roomData.getRoomNumber());
        room.setType(roomData.getType());
        room.setPrice(roomData.getPrice());
        room.setAvailableForDates(roomData.isAvailableForDates());
        roomService.saveRoom(room);

        redirectAttributes.addAttribute("userId", userId);
        return "redirect:/manager/hotels/" + room.getHotel().getId();
    }

    @GetMapping("/hotels/{hotelId}/rooms/create")
    public String showCreateRoomForm(
            @PathVariable Long hotelId,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        Room room = new Room();
        room.setHotel(hotelService.getHotelByIdAndOwnerId(hotelId, userId));
        model.addAttribute("room", room);
        model.addAttribute("userId", userId);
        return "manager-create-room"; // нова HTML-сторінка
    }

    // Обробити створення нової кімнати
    @PostMapping("/hotels/{hotelId}/rooms/create")
    public String createRoom(
            @PathVariable Long hotelId,
            @RequestParam("userId") Long userId,
            @ModelAttribute("room") Room room,
            RedirectAttributes redirectAttributes
    ) {
        Hotel hotel = hotelService.getHotelByIdAndOwnerId(hotelId, userId);
        room.setHotel(hotel);
        roomService.saveRoom(room);

        redirectAttributes.addAttribute("userId", userId);
        return "redirect:/manager/hotels/{hotelId}";
    }

    @GetMapping("/create-hotel")
    public String showCreateHotelForm(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
            Model model
    ) {
        User user = userService.getUserByEmail(securityUser.getUsername());
        CreateHotelWithRoomsDTO dto = new CreateHotelWithRoomsDTO();
        dto.getRooms().add(new CreateRoomDTO());
        model.addAttribute("hotelDTO", dto);
        model.addAttribute("userId", user.getId());
        return "create-hotel";
    }

    @PostMapping("/create-hotel")
    public String createHotel(
            @ModelAttribute("hotelDTO") CreateHotelWithRoomsDTO hotelDTO,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
            RedirectAttributes redirectAttributes
    ) {
        // Перевірка автентифікації
        if (securityUser == null) {
            redirectAttributes.addFlashAttribute("error", "Будь ласка, увійдіть у систему");
            return "redirect:/login";
        }

        // Отримання користувача
        User user = userService.getUserByEmail(securityUser.getUsername());
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Користувач не знайдений");
            return "redirect:/profile";
        }

        // Встановлення userId
        hotelDTO.setUserId(user.getId());
        System.out.println("Переданий userId: " + hotelDTO.getUserId()); // Логування

        try {
            hotelService.createHotelWithRooms(hotelDTO);
            redirectAttributes.addFlashAttribute("success", "Готель створено!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }
}