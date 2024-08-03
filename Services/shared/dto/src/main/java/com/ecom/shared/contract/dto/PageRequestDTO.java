package com.ecom.shared.contract.dto;

import lombok.*;

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
