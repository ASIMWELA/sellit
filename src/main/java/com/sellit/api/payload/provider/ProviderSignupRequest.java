package com.sellit.api.payload.provider;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class ProviderSignupRequest {
    //personal details
    @Size(min = 2, max = 90, message = "First name should be between 2 and 90 in length")
    @NotEmpty(message = "First name cannot be empty")
    String firstName;
    @Size(min = 2, max = 90, message = "userName must be between 2 and 90")
    @NotEmpty(message = "User name cannot be empty")
    String userName;
    @Size(min = 2, max = 90, message = "lastName must be between 2 and 90")
    @NotEmpty(message = "userName cannot be empty")
    String lastName;
    @Size(min = 2, max = 90, message = "email has to be between 2 and 90 in length")
    @NotEmpty(message = "email cannot be empty")
    @Email(message = "email provided is not valid")
    String email;
    @NonNull
    @Size(min=5, max = 15, message = "password must be between 5 and 15 characters")
    @NotEmpty(message = "password cannot be blank")
    String password;
    @Size(min = 2, max = 90, message = "mobile number should be between 2 and 90 in length")
    @NonNull
    @NotEmpty(message = "mobileNumber cannot be blank")
    String mobileNumber;
    //business details
    @NotNull
    boolean isIndividual;
    @NotNull
    boolean isRegisteredOffice;
    @Size(min = 2, max = 500, message = "office address description should be between 2 and 500 in length")
    @NotEmpty(message = "officeAddress cannot be blank")
    String officeAddress;
    @Size(min = 2, max = 500, message = "Business description be between 2 and 500 in length")
    @NotEmpty(message = "providerDescription description not be empty")
    String providerDescription;

}
