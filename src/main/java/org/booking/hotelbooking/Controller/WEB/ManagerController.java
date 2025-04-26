package org.booking.hotelbooking.Controller.WEB;

import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Entity.Role;
import org.booking.hotelbooking.Entity.Room;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Service.HotelService;
import org.booking.hotelbooking.Service.RoomService;
import org.booking.hotelbooking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        room.setAvailable(roomData.isAvailable());
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
}