package com.sellit.api.repository;

import com.sellit.api.Entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceAppointmentRepository  extends JpaRepository<ServiceAppointment, Long> {
    Optional<ServiceAppointment> findByUuid(String serviceAppointmentUuid);
}
