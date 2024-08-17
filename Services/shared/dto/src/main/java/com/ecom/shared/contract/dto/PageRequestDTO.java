package com.ecom.shared.contract.dto;

import com.ecom.shared.contract.enums.Operator;
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

    public PageRequestDTO idCriteria(String id){
       return PageRequestDTO.builder().size(1).page(1).andCriteria(List.of(SearchCriteria.builder().key("_id").value(id).operator(Operator.EQUAL).build())).build();
    }
}
