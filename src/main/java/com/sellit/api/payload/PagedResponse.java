package com.sellit.api.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PagedResponse {
    PageMetadata pageMetadata;
    List<?> data;
}
