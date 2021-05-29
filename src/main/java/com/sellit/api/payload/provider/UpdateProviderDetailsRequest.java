package com.sellit.api.payload.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateProviderDetailsRequest {
    boolean isIndividual;
    boolean isRegisteredOffice;
    @Size(min=10, max = 500)
    String officeAddress;
    @Size(min=50,max= 800)
    String providerDescription;
}
