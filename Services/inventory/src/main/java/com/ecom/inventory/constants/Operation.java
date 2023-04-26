package com.ecom.inventory.constants;

public enum Operation {

    ADD("+"),SUB("-");

    private String value;


    Operation(String value) {
        this.value = value;
    }
}
