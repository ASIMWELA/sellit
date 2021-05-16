package com.sellit.api.repository;

import com.sellit.api.Entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    Optional<ServiceCategory> findByServiceCategoryName(String serviceName);
    boolean existsByServiceCategoryName(String serviceCategoryName);

   Optional<ServiceCategory> findByUuid(String uuid);
}
