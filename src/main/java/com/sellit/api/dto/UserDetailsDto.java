package com.sellit.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String firstName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String mobileNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean isEnabled;
}
