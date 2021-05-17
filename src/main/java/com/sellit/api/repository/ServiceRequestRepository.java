package com.sellit.api.repository;

import com.sellit.api.Entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Optional<ServiceRequest> findByUuid(String serviceRequestUuid);
}
