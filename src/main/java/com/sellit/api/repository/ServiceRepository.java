package com.sellit.api.repository;

import com.sellit.api.Entity.Service;
import com.sellit.api.Entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByServiceName(String serviceName);
    boolean existsByServiceName(String serviceName);
    Optional<Service> findByUuid(String uuid);
}
