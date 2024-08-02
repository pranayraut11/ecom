package com.ecom.shared.common.dto;

import lombok.*;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    private List<SearchCriteria> andCriteria;
    private List<SearchCriteria> orCriteria;
    private String orders;
    private int page;
    private int size;

}
