package org.booking.hotelbooking.Controller.API;

import org.booking.hotelbooking.DTO.CreateHotelWithRoomsDTO;
import org.booking.hotelbooking.DTO.CreateRoomDTO;
import org.booking.hotelbooking.Service.AmadeusHotelService;
import org.booking.hotelbooking.Service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;

@RestController
@RequestMapping("/api/hotels")
public class HotelAPIController {

    private final AmadeusHotelService amadeusHotelService;
    private final HotelService hotelService;

    @Autowired
    public HotelAPIController(AmadeusHotelService amadeusHotelService, HotelService hotelService) {
        this.amadeusHotelService = amadeusHotelService;
        this.hotelService = hotelService;
    }

    @PostMapping("/sync")
    public String syncHotels(@RequestParam String cityCode){
        try {
            amadeusHotelService.syncHotelsFromAmadeus(cityCode);
            return "Готелі успішно синхронізовано";
        }catch (Exception e){
            e.printStackTrace();
            return "Помилка під час синхронізації готелів"+e.getMessage();
        }
    }
    @GetMapping("/debug-create")
    public String debugCreate() {
        CreateHotelWithRoomsDTO dto = new CreateHotelWithRoomsDTO();
        dto.setName("Test Hotel");
        dto.setAddress("Main Street");
        dto.setCity("TestCity");
        dto.setCountry("Ukraine");
        dto.setContactInfo("test@email.com");

        CreateRoomDTO room = new CreateRoomDTO();
        room.setRoomNumber("101");
        room.setType("Standard");
        room.setPrice(BigDecimal.valueOf(100));
        room.setAvailableForDates(true);

        dto.setRooms(Collections.singletonList(room));
        hotelService.createHotelWithRooms(dto);
        return "redirect:/";
    }


}
