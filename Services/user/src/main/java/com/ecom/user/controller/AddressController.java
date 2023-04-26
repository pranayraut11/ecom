package com.ecom.user.controller;

import com.ecom.user.model.Address;
import com.ecom.user.service.specification.AddressService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("users/address")
public class AddressController {

    private AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping()
    public void createAddress(@RequestBody Address address){
        addressService.create(address);
    }

    @GetMapping()
    public Set<Address> get(){
        return  addressService.get();
    }

    @GetMapping("/{id}")
    public Set<Address> get(@PathVariable String id){
        return  addressService.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        addressService.delete(id);
    }

}
