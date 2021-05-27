package com.sellit.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceOfferDto {
    OfferPackage offerPackage;
    ProviderDetails providerDetails;
}
