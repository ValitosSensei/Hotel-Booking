package org.booking.hotelbooking.DTO;

import org.booking.hotelbooking.Entity.Room;

import java.math.BigDecimal;

public class RoomDTO {
    private Long id;
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private boolean available;

    public RoomDTO() {

    }

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.type = room.getType();
        this.price = room.getPrice();
        this.available = room.isAvailable();
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
