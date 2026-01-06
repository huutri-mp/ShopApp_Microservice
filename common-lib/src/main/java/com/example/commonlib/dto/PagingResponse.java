package com.example.commonlib.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingResponse<T> {
    List<T> items;
    long total;
    int page;
    int size;
    boolean hasNext;
    boolean hasPrev;
}