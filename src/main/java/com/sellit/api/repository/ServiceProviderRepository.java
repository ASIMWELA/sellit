package com.sellit.api.repository;

import com.sellit.api.Entity.ServiceProvider;
import jdk.nashorn.internal.ir.Optimistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    Optional<ServiceProvider> findByUuid(String uuid);
}
