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


    public RoleRequest() {}

    public RoleRequest(Long id, User user, Role requestRole, LocalDateTime requestDate, boolean isApproved) {
        this.id = id;
        this.user = user;
        this.requestRole = requestRole;
        this.requestDate = requestDate;
        this.isApproved = isApproved;
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
