package com.sellit.api.payload.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;


@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class CustomerSignupRequest {
   @NonNull
   @Valid Customer customer;
   @NonNull
   @Valid UserAddressPayload address;

}
