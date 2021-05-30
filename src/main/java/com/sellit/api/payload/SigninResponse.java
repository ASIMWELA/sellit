package com.sellit.api.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.Entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class SigninResponse {
    TokenPayload tokenPayload;
    User userData;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String serviceProviderUuid;
}
