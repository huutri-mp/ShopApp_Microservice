package com.example.commonlib.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingResponse<T> {
    private List<T> items;
    private long total;
    private int page;
    private int size;
}