package com.ecom.shared.common.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class ApiSubError {

    private String path;

    private String error;

}