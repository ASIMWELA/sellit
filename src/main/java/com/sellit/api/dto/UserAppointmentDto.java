package com.sellit.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAppointmentDto {
 String uuid,
         appointmentDate,
         appointmentStartTime,
         appointmentEndTime,
         appointmentDesc,
         appointmentWith,
         providerPhone,
         providerEmail;
}
