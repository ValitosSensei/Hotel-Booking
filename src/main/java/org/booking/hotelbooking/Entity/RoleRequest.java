package org.booking.hotelbooking.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RoleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Role requestRole;

    private LocalDateTime requestDate;
    private boolean isApproved = false;


    private String hotelName;
    private String hotelAddress;

    private boolean rejected = false;

    // Геттер та сеттер
    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public RoleRequest(Long id, User user, Role requestRole, LocalDateTime requestDate, boolean isApproved, String hotelName, String hotelAddress,
                       boolean rejected) {
        this.id = id;
        this.user = user;
        this.requestRole = requestRole;
        this.requestDate = requestDate;
        this.isApproved = isApproved;
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
        this.rejected = rejected;
    }

    public RoleRequest() {}

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRequestRole() {
        return requestRole;
    }

    public void setRequestRole(Role requestRole) {
        this.requestRole = requestRole;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
