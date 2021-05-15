package com.sellit.api.payload;

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
}
