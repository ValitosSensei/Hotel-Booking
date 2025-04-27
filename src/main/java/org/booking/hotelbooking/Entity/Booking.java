package org.booking.hotelbooking.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime bookingDate;
    private LocalDate  checkInDate;
    private LocalDate  checkOutDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "transferred_from_id")
    private User transferredFrom; // Хто передав бронювання

    @ManyToOne
    @JoinColumn(name = "transferred_to_id")
    private User transferredTo;

    private String confirmationToken;

    public Booking() {}

    public Booking(Long id, LocalDateTime bookingDate, LocalDate checkInDate,
                   LocalDate checkOutDate, BookingStatus status, LocalDateTime createdAt,
                   User user, Room room, User transferredFrom, User transferredTo,
                   String confirmationToken) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.createdAt = createdAt;
        this.user = user;
        this.room = room;
        this.transferredFrom = transferredFrom;
        this.transferredTo = transferredTo;
        this.confirmationToken = confirmationToken;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getTransferredFrom() {
        return transferredFrom;
    }

    public void setTransferredFrom(User transferredFrom) {
        this.transferredFrom = transferredFrom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getTransferredTo() {
        return transferredTo;
    }

    public void setTransferredTo(User transferredTo) {
        this.transferredTo = transferredTo;
    }
}
