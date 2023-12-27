package com.ecom.shared.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Builder
@Data
public class PageResponse {

    private List<Object> data;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private int size;
    private int number;


}
