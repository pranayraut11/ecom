package com.ecom.order.service.implementation;

import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderProductInput;
import com.ecom.order.entity.Order;
import com.ecom.order.entity.OrderStatus;
import com.ecom.order.mapper.OrderMapper;
import com.ecom.order.model.Product;
import com.ecom.order.repository.OrderRepository;
import com.ecom.order.rest.ProductClient;
import com.ecom.order.util.OrderProductUtil;
import com.ecom.order.util.OrderValidationUtil;
import com.ecom.order.util.SnowflakeIdGenerator;
import com.ecom.order.util.UserContextUtil;
import com.ecom.shared.contract.dto.PageRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {
    @Mock
    private ProductClient productClients;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Mock
    private UserContextUtil userContextUtil;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private HttpServletRequest httpServletRequest;
    
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // We won't set up default mocks in the setUp method to avoid Mockito issues
        // Each test will set up its own mocks as needed
    }
    
    // Helper method to create basic order DTO with valid values
    private CreateOrderDTO createBasicOrderDTO() {
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setAddressId("addr123");
        createOrderDTO.setPaymentMode("CREDIT_CARD");
        createOrderDTO.setPaymentProvider("STRIPE");
        return createOrderDTO;
    }
    
    // Helper method to create a product input with basic values
    private OrderProductInput createProductInput(String productId, String sellerId, int quantity) {
        OrderProductInput input = new OrderProductInput();
        input.setProductId(productId);
        input.setSellerId(sellerId);
        input.setQuantity(quantity);
        return input;
    }
    
    // Helper method to create a product with basic values
    private Product createProduct(String productId, String sellerId, boolean inStock, boolean active) {
        Product product = new Product();
        product.setProductId(productId);
        product.setSellerId(sellerId);
        product.setInStock(inStock);
        product.setActive(active);
        product.setPrice(new BigDecimal("100.00"));
        return product;
    }
    
    // Helper method to set up the OrderService spy
    private OrderServiceImpl setupOrderServiceSpy(String userId) {
        OrderServiceImpl spyOrderService = spy(orderService);
        doReturn(userId).when(spyOrderService).getUserIdFromRequest();
        return spyOrderService;
    }
    
    // Helper method to verify exception message contains expected text
    private void assertExceptionMessageContains(Exception exception, String expectedText) {
        assertTrue(exception.getMessage().toLowerCase().contains(expectedText.toLowerCase()),
                "Expected exception message to contain '" + expectedText + "' but was: " + exception.getMessage());
    }

    @Test
    void testCreateOrder_success() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        OrderProductInput input = createProductInput("p1", "s1", 1);
        createOrderDTO.setProducts(List.of(input));

        Product product = createProduct("p1", "s1", true, true);
        List<Product> foundProducts = List.of(product);

        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(foundProducts);
        when(orderMapper.toOrder(any(), anyList())).thenReturn(new Order());
        when(snowflakeIdGenerator.nextId()).thenReturn(12345L);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        String orderId = spyOrderService.createOrder(createOrderDTO);
        
        // Assert
        assertNotNull(orderId);
        assertEquals("12345", orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_productNotFound() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        OrderProductInput input = createProductInput("p1", "s1", 1);
        createOrderDTO.setProducts(List.of(input));

        // No products returned from productClients
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of());
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "Product not found");
    }

    @Test
    void testCreateOrder_invalidQuantity() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        OrderProductInput input = createProductInput("p1", "s1", -1); // Invalid quantity
        createOrderDTO.setProducts(List.of(input));

        Product product = createProduct("p1", "s1", true, true);
        List<Product> foundProducts = List.of(product);

        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(foundProducts);
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "Invalid quantity");
    }

    @Test
    void testCreateOrder_productOutOfStock() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        OrderProductInput input = createProductInput("p1", "s1", 1);
        createOrderDTO.setProducts(List.of(input));
        
        Product product = createProduct("p1", "s1", false, true); // Simulate out of stock
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "stock");
    }

    @Test
    void testCreateOrder_duplicateProducts() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        
        OrderProductInput input1 = createProductInput("p1", "s1", 1);
        OrderProductInput input2 = createProductInput("p1", "s1", 2);
        
        createOrderDTO.setProducts(List.of(input1, input2));
        
        Product product = createProduct("p1", "s1", true, true);
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "duplicate");
    }

    @Test
    void testCreateOrder_invalidSeller() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        
        OrderProductInput input = createProductInput("p1", "s2", 1); // Seller does not match
        
        createOrderDTO.setProducts(List.of(input));
        
        Product product = createProduct("p1", "s1", true, true); // Different seller ID
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "seller");
    }


    @Test
    void testCreateOrder_emptyProductList() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        createOrderDTO.setProducts(List.of()); // Empty list
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "empty");
    }

    @Test
    void testCreateOrder_invalidUserContext() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        
        OrderProductInput input = createProductInput("p1", "s1", 1);
        
        createOrderDTO.setProducts(List.of(input));
        
        // Simulate getUserIdFromRequest throwing exception
        OrderServiceImpl spyOrderService = spy(orderService);
        doThrow(new IllegalArgumentException("Invalid user context")).when(spyOrderService).getUserIdFromRequest();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "user context");
    }

    @Test
    void testCreateOrder_invalidAddress() {
        // Arrange
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        // Don't use createBasicOrderDTO to set invalid address
        createOrderDTO.setAddressId(null); // Invalid address
        createOrderDTO.setPaymentMode("CREDIT_CARD");
        createOrderDTO.setPaymentProvider("STRIPE");
        
        OrderProductInput input = createProductInput("p1", "s1", 1);
        
        createOrderDTO.setProducts(List.of(input));
        
        Product product = createProduct("p1", "s1", true, true);
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "address");
    }

    @Test
    void testCreateOrder_inactiveProduct() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        
        OrderProductInput input = createProductInput("p1", "s1", 1);
        
        createOrderDTO.setProducts(List.of(input));
        
        Product product = createProduct("p1", "s1", true, false); // Inactive product
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "inactive");
    }

    @Test
    void testCreateOrder_invalidCoupon() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        createOrderDTO.setCouponCode("INVALID"); // Invalid coupon
        
        OrderProductInput input = createProductInput("p1", "s1", 1);
        
        createOrderDTO.setProducts(List.of(input));
        
        Product product = createProduct("p1", "s1", true, true);
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "coupon");
    }

    @Test
    void testCreateOrder_crossSellerRestriction() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        
        OrderProductInput input1 = createProductInput("p1", "s1", 1);
        OrderProductInput input2 = createProductInput("p2", "s2", 1);
        
        createOrderDTO.setProducts(List.of(input1, input2));
        
        Product product1 = createProduct("p1", "s1", true, true);
        Product product2 = createProduct("p2", "s2", true, true);
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product1, product2));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "cross-seller");
    }

    @Test
    void testCreateOrder_priceMismatch() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        
        OrderProductInput input = createProductInput("p1", "s1", 1);
        input.setPrice(new BigDecimal("100.00")); // Price in input
        
        createOrderDTO.setProducts(List.of(input));
        
        Product product = createProduct("p1", "s1", true, true);
        product.setPrice(new BigDecimal("120.00")); // Different price
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(List.of(product));
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "price");
    }

    @Test
    void testCreateOrder_orderSizeLimit() {
        // Arrange
        CreateOrderDTO createOrderDTO = createBasicOrderDTO();
        
        List<OrderProductInput> inputs = new java.util.ArrayList<>();
        for (int i = 0; i < 101; i++) { // Assuming 100 is the max allowed
            OrderProductInput input = createProductInput("p"+i, "s1", 1);
            inputs.add(input);
        }
        createOrderDTO.setProducts(inputs);
        
        List<Product> foundProducts = new java.util.ArrayList<>();
        for (int i = 0; i < 101; i++) {
            Product product = createProduct("p"+i, "s1", true, true);
            foundProducts.add(product);
        }
        
        when(productClients.getProducts(any(PageRequestDTO.class))).thenReturn(foundProducts);
        
        // Mock the service methods
        OrderServiceImpl spyOrderService = setupOrderServiceSpy("user123");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyOrderService.createOrder(createOrderDTO);
        });
        assertExceptionMessageContains(exception, "order size");
    }
}
