package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.DTO.CreateHotelWithRoomsDTO;
import org.booking.hotelbooking.DTO.HotelWithRoomsDTO;
import org.booking.hotelbooking.DTO.RoomDTO;
import org.booking.hotelbooking.Entity.Hotel;
import org.booking.hotelbooking.Entity.Room;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Repository.HotelRepository;
import org.booking.hotelbooking.Repository.RoomRepository;
import org.booking.hotelbooking.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public HotelService(HotelRepository hotelRepository,
                        RoomRepository roomRepository, UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public List<Hotel> listHotel(){
        return hotelRepository.findAll();
    }

    @Transactional
    public void createHotelWithRooms(CreateHotelWithRoomsDTO dto) {
        User manager = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setContactInfo(dto.getContactInfo());
        hotel.setOwner(manager);

        hotel = hotelRepository.save(hotel); // Зберегти готель перед додаванням кімнат

        Hotel savedHotel = hotel; // створюємо effectively final змінну для використання в лямбді

        List<Room> rooms = dto.getRooms().stream().map(roomDTO -> {
            Room room = new Room();
            room.setHotel(savedHotel); // використовуємо savedHotel
            room.setRoomNumber(roomDTO.getRoomNumber());
            room.setType(roomDTO.getType());
            room.setPrice(roomDTO.getPrice());
            room.setAvailableForDates(roomDTO.isAvailableForDates());
            return room;
        }).collect(Collectors.toList());

        hotel.setRooms(rooms);
        hotelRepository.save(hotel); // Оновити готель з кімнатами
    }

    public List<String> getAllCities() {
        return hotelRepository.findDistinctCities();
    }

    public List<Hotel> getHotelsByCity(String city) {
        if (city == null || city.isEmpty()) {
            return hotelRepository.findAll();
        }
        return hotelRepository.findByCity(city);
    }


    public List<Hotel> getHotelsByCityAndSortByRating(String city, String sortOrder, Double minRating, Double maxRating) {
        List<Hotel> hotels;

        if (city != null && !city.isEmpty()) {
            hotels = hotelRepository.findByCity(city);
        } else {
            hotels = hotelRepository.findAll();
        }

        // Фільтруємо по мінімальному та максимальному рейтингу
        if (minRating != null) {
            hotels = hotels.stream()
                    .filter(hotel -> hotel.getAverageRating() >= minRating)
                    .collect(Collectors.toList());
        }

        if (maxRating != null) {
            hotels = hotels.stream()
                    .filter(hotel -> hotel.getAverageRating() <= maxRating)
                    .collect(Collectors.toList());
        }

        // Сортуємо за рейтингом від більшого до меншого або навпаки
        if ("desc".equalsIgnoreCase(sortOrder)) {
            hotels.sort((h1, h2) -> Double.compare(h2.getAverageRating(), h1.getAverageRating()));
        } else if ("asc".equalsIgnoreCase(sortOrder)) {
            hotels.sort((h1, h2) -> Double.compare(h1.getAverageRating(), h2.getAverageRating()));
        }

        return hotels;
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id).
                orElseThrow(()->new RuntimeException("Hotel not found"));

    }

    @Transactional
    public void UpdateHotel(Long hotelId, Hotel hotelDetails) {
        Hotel hotel = getHotelById(hotelId);
        hotel.setName(hotelDetails.getName());
        hotel.setAddress(hotelDetails.getAddress());
        hotel.setCity(hotelDetails.getCity());
        hotel.setCountry(hotelDetails.getCountry());
        hotel.setContactInfo(hotelDetails.getContactInfo());
        hotelRepository.save(hotel);
    }

    @Transactional
    public void deleteHotel(Long hotelId) {
        hotelRepository.deleteById(hotelId);
    }

    public List<Hotel> getHotelsByManagerId(Long managerId) {
        return hotelRepository.findByOwnerId(managerId);
    }

    public Hotel getHotelByIdAndOwnerId(Long hotelId, Long ownerId) {
        return hotelRepository.findByIdAndOwnerId(hotelId, ownerId)
                .orElseThrow(() -> new RuntimeException("Готель не знайдено або доступ заборонено"));
    }



}
