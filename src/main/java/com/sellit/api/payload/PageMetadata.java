package com.sellit.api.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PageMetadata {
    int pageSize;
    int pageNumber;
    int numberOfRecordsOnPage;
    long totalNumberOfRecords;
    boolean lastPage;
    boolean firstPage;
    boolean hasNext;
    boolean hasPrevious;

}
