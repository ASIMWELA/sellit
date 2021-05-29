package com.sellit.api.payload.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserAddressRequest {
    @Size(min= 2,max = 100)
    String city;
    @Size(min= 2,max = 60)
    String country;
    @Size(min = 2, max = 60)
    String region;
    @Size(min = 2, max = 60)
    String street;
    @Size(min = 10, max = 500)
    String locationDescription;
}
