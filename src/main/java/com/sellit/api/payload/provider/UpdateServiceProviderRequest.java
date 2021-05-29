package com.sellit.api.payload.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateServiceProviderRequest {
    double billingRatePerHour;
    @Size(min=1, message = "experienceInMonths should not be less than 1")
    int experienceInMonths;
    @Size(min=2, max = 800)
    @NotEmpty(message = "service offer description cannot be empty")
    String serviceOfferingDescription;
}
