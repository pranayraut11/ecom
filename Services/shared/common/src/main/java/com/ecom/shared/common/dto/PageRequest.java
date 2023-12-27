package com.ecom.shared.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
public class PageRequest extends org.springframework.data.domain.PageRequest {
    private List<SearchCriteria> andCriteria;
    private List<SearchCriteria> orCriteria;
    private String orders;

    PageRequest(int page, int size,String orders){

        super(page,size, Sort.by(orders));
    }

}
