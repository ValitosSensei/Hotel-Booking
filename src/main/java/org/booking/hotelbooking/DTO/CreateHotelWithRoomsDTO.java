package org.booking.hotelbooking.DTO;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class CreateHotelWithRoomsDTO {
    private String name;
    private String address;
    private String city;
    private String country;
    private String contactInfo;
    private List<CreateRoomDTO> rooms = new ArrayList<>();
    private Long userId;

    private List<String> photoUrls = new ArrayList<>();
    private List<MultipartFile> photos = new ArrayList<>(); // Замінити photoUrls на photos

    public List<MultipartFile> getPhotos() {
        return photos;
    }

    public void setPhotos(List<MultipartFile> photos) {
        this.photos = photos;
    }
    public Long getUserId() {
        return userId;
    }
    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<CreateRoomDTO> getRooms() {
        return rooms;
    }

    public void setRooms(List<CreateRoomDTO> rooms) {
        this.rooms = rooms;
    }
}
