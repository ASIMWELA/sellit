package com.sellit.api.payload.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserUpdateRequest {
    String firstName;
    @Size(min = 5)
    String userName;
    String lastName;
    @Email(message = "Email provided is not valid")
    String email;
    @Size(min=5, max = 15, message = "password must be between 5 and 15 characters")
    String password;
    String mobileNumber;
}
