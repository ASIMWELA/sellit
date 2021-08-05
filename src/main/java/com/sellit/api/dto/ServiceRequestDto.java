package com.sellit.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ServiceRequestDto {
    String uuid,
            requestDescription,
            requiredDate,
            expectedStartTime,
            requestBy,
            email,
            locationCity,
            country;
    Long expectedHours;
}
