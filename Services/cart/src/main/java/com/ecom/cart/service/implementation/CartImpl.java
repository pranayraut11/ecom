package com.ecom.cart.service.implementation;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.Product;
import com.ecom.cart.repository.CartRepository;
import com.ecom.cart.service.specification.CartService;
import lombok.extern.slf4j.Slf4j;
import org.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
        cartRepository.deleteAll();
        return cartRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {

        Cart cart = cartRepository.findByUserId("pranay");
        if (cart != null) {
            Product product = cart.getProducts().stream().filter(cartProduct -> cartProduct.getProductId().equals(id)).collect(Collectors.toList()).stream().findFirst().get();
            cart.getProducts().remove(product);
            if (cart.getProducts().size() == 0) {
                cartRepository.delete(cart);
            } else {
                cartRepository.save(cart);
            }
        }
    }

    @Override
    public Cart create(Cart entity) {
        Cart cart = cartRepository.findByUserId(entity.getUserId());
        if (cart != null) {
            boolean isProductExist = false;
            for (Product product : cart.getProducts()) {
                if (product.getProductId().equals(entity.getProducts().get(0).getProductId())) {
                    log.info("Product already exist in cart . incrementing quantity");
                    product.setQuantity((short) (product.getQuantity() + 1));
                    isProductExist = true;
                    break;
                }
            }
            if (!isProductExist) {
                cart.getProducts().add(entity.getProducts().get(0));
            }
        } else {
            cart = entity;
            cart.setId(UUID.randomUUID().toString());
        }
        return cartRepository.save(cart);
    }

    @Override
    public Cart update(Cart entity) {
        return cartRepository.save(entity);
    }

    @Override
    public Cart getCart() {
        return cartRepository.findByUserId("pranay");
    }

    @Override
    public Cart addProductToCart(Product product) {
        Cart cart = cartRepository.findByUserId("pranay");
        if (Objects.isNull(cart)) {
            cart = Cart.builder().products(Arrays.asList(product)).userId("pranay").build();
            cart.setId(UUID.randomUUID().toString());
        } else if (cart.getProducts() != null) {
            boolean isProductExist = false;
            for (Product cartProduct : cart.getProducts()) {
                if (cartProduct.getProductId().equals(product.getProductId())) {
                    log.info("Product already exist in cart . incrementing quantity");
                    cartProduct.setQuantity((short) (cartProduct.getQuantity() + 1));
                    isProductExist = true;
                    break;
                }
            }
            if (!isProductExist) {
                cart.getProducts().add(product);
            }
        }
        return cartRepository.save(cart);
    }
}
