package com.ecom.cart.service.implementation;

import com.ecom.cart.repository.CartRepository;
import com.ecom.cart.service.specification.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Override
    public void delete(String id) {
        log.info("Deleting cart {} ...",id);
        cartRepository.deleteById(id);
        log.info("Cart {} deleted");
    }
}
