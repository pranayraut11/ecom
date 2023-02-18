package com.ecom.user.service.specification;

import com.ecom.user.model.Address;

import java.util.List;
import java.util.Set;

public interface AddressService {

    public void create(Address address);

    public Set<Address> get();

    public Address get(String id);

    public void delete(String id);
}
