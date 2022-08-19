package com.ecom.cart.repository;

import com.ecom.cart.entity.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends CrudRepository<Cart, String> {


    Cart findByUserIdAndProducts_ProductId(String userId, String productId);

    Cart findByUserId(String userId);

}
