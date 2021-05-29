package com.sellit.api.payload.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class UserAddressPayload {
    @NotEmpty(message = "region cannot be empty")
    @Size(min = 2, message = "city should have a tleast 2 characters")
    String city;
    @NotEmpty(message = "region cannot be empty")
    @Size(min = 2, message = "country should hv at least have 2 characters")
    String country;
    @NotEmpty(message = "region cannot be empty")
    @Size(min = 5, message = "region should be at least 5 characters")
    String region;
    @NotEmpty(message = "street cannot be empty")
    @Size(min=4, message = "street should at least have 5 characters")
    String street;
    @NotEmpty(message = "locationDescription cannot be empty")
    @Size(min=20, message = "locationDescription should at least have 20 characters")
    String locationDescription;
}
