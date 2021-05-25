package com.sellit.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsDto {
    String userName;
    String firstName;
    String lastName;
    String email;
    String mobileNumber;
}
