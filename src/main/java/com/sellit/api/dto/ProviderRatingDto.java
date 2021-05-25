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
public class ProviderRatingDto {
    double avgPunctualityRating,
     avgProficiencyRating,
     avgProfessionalismRating,
     avgCommunicationRating,
     avgPriceRating,
     overallRating;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date updatedOn;
}
