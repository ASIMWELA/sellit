package com.sellit.api.repository;

import com.sellit.api.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByServiceName(String serviceName);
    boolean existsByServiceName(String serviceName);
    Optional<Service> findByUuid(String uuid);
}
