package org.booking.hotelbooking.DTO;

import java.math.BigDecimal;

public class CreateRoomDTO {
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private boolean availableForDates;

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


    public boolean isAvailableForDates() {
        return availableForDates;
    }

    public void setAvailableForDates(boolean availableForDates) {
        this.availableForDates = availableForDates;
    }
}
