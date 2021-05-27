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
public class OfferPackage {
     @JsonInclude(JsonInclude.Include.NON_NULL)
     String uuid;
     double  discountInPercent;
     double estimatedCost;
     @JsonInclude(JsonInclude.Include.NON_NULL)
     Date offerSubmissionDate;
}
