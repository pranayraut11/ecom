package com.ecom.shared.contract.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class ApiSubError {

    private String path;

    private String error;

}