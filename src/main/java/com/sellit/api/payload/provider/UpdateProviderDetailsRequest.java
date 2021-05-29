package com.sellit.api.payload.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateProviderDetailsRequest {
    boolean isIndividual;
    boolean isRegisteredOffice;
    @Size(min=10, max = 500, message = "officeAddress should be between 10 and 500 words")
    @NotEmpty(message = "Office address cannot be empty")
    String officeAddress;
    @Size(min=50,max= 800, message = "providerDescription should be between 50 and 800 words")
    String providerDescription;
}
