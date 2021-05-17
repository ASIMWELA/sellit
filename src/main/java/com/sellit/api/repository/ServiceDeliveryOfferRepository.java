package com.sellit.api.repository;

import com.sellit.api.Entity.ServiceDeliveryOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceDeliveryOfferRepository extends JpaRepository<ServiceDeliveryOffer, Long> {
     Optional<ServiceDeliveryOffer> findByUuid(String serviceDeliveryOfferUuid);
}
