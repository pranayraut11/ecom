package com.ecom.user.model;

import com.ecom.user.constant.enums.AddressType;
import lombok.Data;

import java.util.Objects;

@Data
public class Address {

    private String id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String addressLine1;
    private String addressLine2;
    private String pincode;
    private String city;
    private String state;
    private String landmark;
    private AddressType addressType;
    private boolean defaultAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return id.equals(address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
