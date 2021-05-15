package com.sellit.api.payload.provider;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class ProviderSignupRequest {

    //personal details
    @NotBlank(message = "first name cannot be empty")
    String firstName;
    @NotBlank(message = "uuid name cannot be empty")
    String uuid;
    @NotBlank(message = "user name cannot be empty")
    String userName;
    @NotBlank(message = "last name cannot be empty")
    String lastName;
    @NotBlank(message = "email cannot be empty")
    @Email(message = "Email provided is not valid")
    String email;
    @NotBlank(message = "password cannot be empty")
    @Size(min=5, max = 15, message = "password must be between 5 and 15 characters")
    String password;
    @NotBlank(message = "mobile number cannot be empty")
    String mobileNumber;
    @NotBlank(message = "is a provider cannot be empty")
    boolean isAProvider;

    //business details
    @NotBlank(message = "this is required number cannot be empty")
    boolean isIndividual;
    @NotEmpty(message = "registered office cannot be empty")
    boolean isRegisteredOffice;
    @NotBlank(message = "office address cannot be empty")
    String officeAddress;
    @NotBlank(message = "business description cannot be empty")
    String providerDescription;

}
