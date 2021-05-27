package com.sellit.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProviderDetails {
     double billingRatePerHour;
     int experienceInMonths;
     String serviceOfferingDescription,  officeAddress;
     @JsonInclude(JsonInclude.Include.NON_NULL)
     UserDetailsDto personalDetails;
     @JsonInclude(JsonInclude.Include.NON_NULL)
     ProviderRatingDto providerRating;
}
