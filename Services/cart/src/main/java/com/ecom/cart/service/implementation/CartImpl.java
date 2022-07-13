package com.ecom.cart.service.implementation;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.repository.CartRepository;
import com.ecom.cart.service.specification.CartService;
import org.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartImpl extends BaseService<Cart> implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public List<Cart> getAll() {
        List<Cart> carts = new ArrayList<>();
        cartRepository.findAll().forEach(carts::add);
        return carts;
    }

    @Override
    public Cart get(String id) {
        return cartRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        cartRepository.deleteById(id);
    }

    @Override
    public Cart create(Cart entity) {
        return cartRepository.save(entity);
    }

    @Override
    public Cart update(Cart entity) {
        return cartRepository.save(entity);
    }
}
