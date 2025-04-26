package org.booking.hotelbooking.DTO;

import org.booking.hotelbooking.Entity.Room;

import java.math.BigDecimal;

public class RoomDTO {
    private Long id;
    private String roomNumber;
    private String type;
    private BigDecimal price;

    private boolean availableForDates;

    public RoomDTO() {

    }

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.type = room.getType();
        this.price = room.getPrice();
        this.availableForDates = room.isAvailableForDates();

    }

    public boolean isAvailableForDates() {
        return availableForDates;
    }

    public void setAvailableForDates(boolean availableForDates) {
        this.availableForDates = availableForDates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
