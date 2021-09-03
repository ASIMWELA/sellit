package com.sellit.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceProviderDto {
    String uuid,
            avgPunctualityRating,
            avgCommunicationRating,
            avgPriceRating,
            overallRating,
            billingRatePerHour,
            experienceInMonths,
            providerName,
            providerEmail;
}
