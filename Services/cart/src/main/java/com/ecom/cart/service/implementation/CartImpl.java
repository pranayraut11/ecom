package com.ecom.cart.service.implementation;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.service.specification.CartService;
import org.ecom.shared.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CartImpl extends BaseService<Cart> implements CartService {
    @Override
    public List<Cart> getAll() {
        return Collections.emptyList();
    }

    @Override
    public Cart get(String id) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public Cart create(Cart entity) {
        return null;
    }

    @Override
    public Cart update(Cart entity) {
        return null;
    }
}
