package org.booking.hotelbooking.Repository;

import org.booking.hotelbooking.Entity.RoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {
    List<RoleRequest> findByIsApprovedFalse();
    List<RoleRequest> findByIsApprovedFalseAndRejectedFalse();
}
