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
public class CustomerSignupRequest {
   @NotBlank(message = "customer cannot be empty")
   Customer customer;
   @NotBlank(message = "customer cannot be empty")
   UserAddressPayload address;

}
