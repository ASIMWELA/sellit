package com.sellit.api.payload.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class UserAddressPayload {
    @NotBlank(message = "region cannot be empty")
    String city;
    @NotBlank(message = "region cannot be empty")
    String country;
    @NotBlank(message = "region cannot be empty")
    String region;
    @NotBlank(message = "street cannot be empty")
    String street;
    @NotBlank(message = "locationDescription cannot be empty")
    String locationDescription;
}
