package com.sellit.api.payload;


import com.sellit.api.Entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PagedResponse {
    List<User> userList;
    PageMetadata pageMetadata;
}
