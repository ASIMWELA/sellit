package com.sellit.api.payload.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class UserAddressPayload {
    @NotBlank(message = "region cannot be empty")
    @NonNull
    String city;
    @NotBlank(message = "region cannot be empty")
    @NonNull
    String country;
    @NotBlank(message = "region cannot be empty")
    @NonNull
    String region;
    @NotBlank(message = "street cannot be empty")
    @NonNull
    String street;
    @NotBlank(message = "locationDescription cannot be empty")
    @NonNull
    String locationDescription;
}
