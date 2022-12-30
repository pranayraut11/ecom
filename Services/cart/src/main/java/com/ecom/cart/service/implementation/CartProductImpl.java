package com.ecom.cart.service.implementation;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.Product;
import com.ecom.cart.repository.CartRepository;
import com.ecom.cart.service.specification.CartProductService;
import com.ecom.cart.utility.PriceCalculationUtil;
import com.ecom.shared.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartProductImpl extends BaseService<Cart> implements CartProductService {

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
        return cartRepository.save(entity);
    }

    @Override
    public Cart update(Cart entity) {
        return cartRepository.save(entity);
    }

    @Override
    public Cart getCart() {
        Cart cart = cartRepository.findByUserId("pranay");
        PriceCalculationUtil.getCartPrice(cart);
        return cart;
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
        return PriceCalculationUtil.getCartPrice(cartRepository.save(cart));
    }

    @Override
    public Cart updateProduct(Product product) {
        Cart cart = cartRepository.findByUserId("pranay");
        if (Objects.nonNull(cart)) {
            List<Product> matchedProduct = cart.getProducts().stream().filter(cartProduct -> cartProduct.getProductId().equals(product.getProductId())).collect(Collectors.toList());
            cart.getProducts().removeAll(matchedProduct);
            cart.getProducts().add(product);
        }
        return PriceCalculationUtil.getCartPrice(cartRepository.save(cart));
    }

    @Override
    public Cart removeProductFromCart(String id) {
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
        return cart;
    }
}
