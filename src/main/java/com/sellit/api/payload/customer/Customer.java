package com.sellit.api.payload.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class Customer {
    //customer details
    @NotBlank(message = "first name cannot be empty")
    @Size(min = 2, message = "firstName should not be less than 2 characters")
    String firstName;
    @NotBlank(message = "uuid name cannot be empty")
    @Size(min=3, message = "userName  should hv a minimum of 3 characters")
    String userName;
    @NotBlank(message = "last name cannot be empty")
    @Size(min=3, message = "lastName should have at least 3 characters")
    String lastName;
    @NotBlank(message = "email cannot be empty")
    @Email(message = "Email provided is not valid")
    String email;
    @NotBlank(message = "password cannot be empty")
    @Size(min=5, max = 15, message = "password must be between 5 and 15 characters")
    String password;
    @NotBlank(message = "mobile number cannot be empty")
    @Size(min=10, message = "mobileNumber should have at least 10 characters")
    String mobileNumber;

}
