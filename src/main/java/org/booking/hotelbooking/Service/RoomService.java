package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.DTO.RoomDTO;
import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Entity.Room;
import org.booking.hotelbooking.Repository.HotelRepository;
import org.booking.hotelbooking.Repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    public List<RoomDTO> getRoomInHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        List<Room> rooms = hotel.getRooms();
        if (rooms == null || rooms.isEmpty()) {
            throw new RuntimeException("No rooms found for hotel ID: " + hotelId);
        }

        return rooms.stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Кімнату не знайдено"));
    }

    public void saveRoom(Room room) {
        roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsInHotelWithFilter(
            Long hotelId,
            String availabilityFilter,
            LocalDate checkIn,
            LocalDate checkOut) {

        List<Room> rooms;

        boolean hasDates = (checkIn != null && checkOut != null);

        if (hasDates) {
            switch (availabilityFilter) {
                case "available" -> rooms = roomRepository.findAvailableRoomsByDates(hotelId, checkIn, checkOut);
                case "occupied" -> rooms = roomRepository.findOccupiedRoomsByDates(hotelId, checkIn, checkOut);
                default -> rooms = roomRepository.findByHotelId(hotelId);
            }
        } else {
            // Якщо дати не вказані, відображаємо всі кімнати готелю
            rooms = roomRepository.findByHotelId(hotelId);
        }

        return rooms.stream()
                .map(room -> {
                    RoomDTO dto = new RoomDTO(room);
                    if (hasDates) {
                        dto.setAvailableForDates(isRoomAvailable(room.getId(), checkIn, checkOut));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.isRoomAvailable(roomId, checkIn, checkOut);
    }

    @Transactional
    public void saveRoomsForHotel(Hotel hotel, List<RoomDTO> roomDTOs) {
        for (RoomDTO roomDTO : roomDTOs) {
            Room room = new Room();
            room.setRoomNumber(roomDTO.getRoomNumber());
            room.setType(roomDTO.getType());
            room.setPrice(roomDTO.getPrice());
            room.setAvailableForDates(roomDTO.isAvailableForDates());
            room.setHotel(hotel);
            roomRepository.save(room);
        }
    }

    public List<Room> getAllRooms(){
        return roomRepository.findAll();
    }



    @Transactional
    public void updateRoomHotel(Long roomId, RoomDTO roomDTO) {
        Room room = getRoomById(roomId);
        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setType(roomDTO.getType());
        room.setPrice(roomDTO.getPrice());
        room.setAvailableForDates(roomDTO.isAvailableForDates());
        roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }

}
