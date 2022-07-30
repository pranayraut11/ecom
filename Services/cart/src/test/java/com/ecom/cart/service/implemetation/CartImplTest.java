//package com.ecom.cart.service.implemetation;
//
//import com.ecom.cart.entity.Cart;
//import com.ecom.cart.service.implementation.CartImpl;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//
//public class CartImplTest {
//
//    @InjectMocks
//    private CartImpl cart;
//
//
//    @BeforeEach
//    void init() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void test() {
//        List<Cart> carts = cart.getAll();
//        Assertions.assertNotNull(carts);
//        Assertions.assertEquals(0,carts.size());
//        Assertions.assertNotNull(carts);
//    }
//
//}
