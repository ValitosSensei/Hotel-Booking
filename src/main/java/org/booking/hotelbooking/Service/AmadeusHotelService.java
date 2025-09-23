package org.booking.hotelbooking.Service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Hotel;  // Амадеус готель
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.booking.hotelbooking.Entity.Review;
import org.booking.hotelbooking.Entity.Room;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Repository.HotelRepository;
import org.booking.hotelbooking.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AmadeusHotelService {
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public AmadeusHotelService(HotelRepository hotelRepository,
                               UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
    }

    public void syncHotelsFromAmadeus(String cityCode) throws Exception {
        Amadeus amadeus = Amadeus.builder("oKxXTG1SA5Wi6LkKmt6cciDHPv65AdEA", "gsS0NPnEewVbR2oG").build();

        User systemUser = userRepository.findByUsername("system-bot").orElseThrow(()->new Exception("System user not found"));

        Hotel[] hotels = amadeus.referenceData.locations.hotels.byCity.get(
                Params.with("cityCode", cityCode)
                        .and("radius", 50)
                        .and("radiusUnit", "KM")
                        .and("hotelSource", "ALL")
        );

        for (Hotel apiHotel : hotels) {
            String name = Optional.ofNullable(apiHotel.getName()).orElse("Unknown Name");
            String address = "Unknown Address";
            String city = "Unknown City";
            String country = "Unknown Country";

            if (apiHotel.getGeoCode() != null) {
                double lat = apiHotel.getGeoCode().getLatitude();
                double lon = apiHotel.getGeoCode().getLongitude();

                String[] location = getCityCountryAndAddressFromCoordinates(lat, lon);
                city = location[0];
                country = location[1];
                address = location[2];
            }

            org.booking.hotelbooking.Entity.Hotel hotel = new org.booking.hotelbooking.Entity.Hotel();  
            hotel.setName(name);
            hotel.setAddress(address);
            hotel.setCity(city);
            hotel.setCountry(country);
            hotel.setContactInfo("Not provided");

            List<String> photos = generateHotelPhotos(3 + random.nextInt(3)); // 3-5 фото
            hotel.setPhotoUrls(photos);


           
            List<Review> reviews = new ArrayList<>();


            Review review = new Review();
            review.setRating(1 + random.nextInt(5)); // Випадковий рейтинг від 1 до 5
            review.setComment("Автоматичний відгук");
            review.setCreatedDate(LocalDateTime.now());
            review.setUser(systemUser);
            review.setHotel(hotel);
            reviews.add(review);

            hotel.setReviews(reviews);

            
            int roomCount = 5 + random.nextInt(6); 
            for (int i = 0; i < roomCount; i++) {
                Room room = new Room();
                room.setRoomNumber("R" + (100 + random.nextInt(900)));
                room.setType(getRandomRoomType());
                room.setPrice(new BigDecimal(50 + random.nextInt(200)));
                room.setAvailableForDates(true);
                room.setHotel(hotel);  // Встановлюємо готель для кімнати
                hotel.getRooms().add(room);
            }
            hotel.setOwner(systemUser);
            hotelRepository.save(hotel);
        }
    }

    private String[] getCityCountryAndAddressFromCoordinates(double lat, double lon) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "hotel-booking-app")
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            String city = extractJsonValue(body, "\"city\":\"", "\"");
            if (city == null) city = extractJsonValue(body, "\"town\":\"", "\"");
            if (city == null) city = extractJsonValue(body, "\"village\":\"", "\"");
            if (city == null) city = "Unknown City";

            String country = extractJsonValue(body, "\"country\":\"", "\"");
            if (country == null) country = "Unknown Country";

            String road = extractJsonValue(body, "\"road\":\"", "\"");
            String houseNumber = extractJsonValue(body, "\"house_number\":\"", "\"");

            String address = "";
            if (road != null) address += road;
            if (houseNumber != null) address += " " + houseNumber;
            if (address.isBlank()) address = "Unknown Address";

            return new String[]{city, country, address};
        } catch (Exception e) {
            return new String[]{"Unknown City", "Unknown Country", "Unknown Address"};
        }
    }

    private String extractJsonValue(String json, String prefix, String suffix) {
        int start = json.indexOf(prefix);
        if (start == -1) return null;
        start += prefix.length();
        int end = json.indexOf(suffix, start);
        return end == -1 ? null : json.substring(start, end);
    }

    private String getRandomRoomType() {
        String[] types = {"Standard", "Deluxe", "Suite", "Single", "Double"};
        return types[random.nextInt(types.length)];
    }
    private List<String> generateHotelPhotos(int count) {
        List<String> photos = new ArrayList<>();
        String accessKey = "hygmPhoerF9qfIg5gpYrsjdG6pMnIOsNt1zzATI4-CM"; 
        try {
            String apiUrl = "https://api.unsplash.com/photos/random?query=hotel&count=" + count + "&client_id=" + accessKey;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject photo = element.getAsJsonObject();
                String url = photo.getAsJsonObject("urls").get("regular").getAsString();
                photos.add(url);
            }
        } catch (Exception e) {
            // Резервний варіант: Picsum з тематичними фото
            for (int i = 0; i < count; i++) {
                int imageId = 1000 + random.nextInt(9000); // ID для різних фото
                photos.add("https://picsum.photos/seed/hotel-" + imageId + "/800/600");
            }
        }
        return photos;
    }
}
