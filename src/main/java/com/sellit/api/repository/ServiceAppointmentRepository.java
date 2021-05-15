package com.sellit.api.repository;

import com.sellit.api.Entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceAppointmentRepository  extends JpaRepository<ServiceAppointment, Long> {
}
