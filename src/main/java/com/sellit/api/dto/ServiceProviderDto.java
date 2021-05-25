package com.sellit.api.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceProviderDto {
 String uuid, serviceOfferingDescription;
 int experienceInMonths;
 double  billingRatePerHour;
 @JsonInclude(JsonInclude.Include.NON_NULL)
 ProviderRatingDto providerRating;
 UserDetailsDto userDetails;
}
