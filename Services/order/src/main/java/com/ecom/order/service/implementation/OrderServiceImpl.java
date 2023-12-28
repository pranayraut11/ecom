package com.ecom.order.service.implementation;

import com.ecom.order.dto.Cart;
import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.InventoryDTO;
import com.ecom.order.dto.OrderDTO;
import com.ecom.order.entity.Order;
import com.ecom.order.model.Product;
import com.ecom.order.repository.OrderRepository;
import com.ecom.order.rest.CartRestService;
import com.ecom.order.rest.InventoryRestService;
import com.ecom.order.rest.ProductRestService;
import com.ecom.order.service.specification.OrderService;
import com.ecom.shared.common.dto.UserDetails;
import com.ecom.shared.common.exception.EcomException;
import com.ecom.shared.common.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends BaseService<Order> implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRestService cartRestService;

    @Autowired
    private ProductRestService productRestService;

    @Autowired
    private InventoryRestService inventoryRestService;

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orders;
    }

    @Override
    public Order get(String id) {
        return orderRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order create(Order entity) {
        return orderRepository.save(entity);
    }

    @Override
    public Order update(Order entity) {
        return orderRepository.save(entity);
    }

    @Override
    public void createOrder(CreateOrderDTO createOrderDTO) {
        String id = createOrderDTO.getId();
        log.info("Creating order for {} and for cart {}  ", UserDetails.getUserId(), id);
        String orderID = UUID.randomUUID().toString();
        List<Product> products = getProducts(createOrderDTO);

        //Check inventory
        List<InventoryDTO> productsWithSeller = products.stream().map(product -> InventoryDTO.builder().productId(product.getProductId()).userId(product.getSeller().getSellerId()).build()).collect(Collectors.toList());
        List<String> unavailableProducts = inventoryRestService.checkStockAvailability(productsWithSeller);
        // If all products are available then create order otherwise throw an error
        if (unavailableProducts.isEmpty()) {
            // Build order object
            Order order = Order.builder().orderId(orderID).userId("pranay1@gmail.com").products(products).build();
            // Save order object
            log.info("Saving order for id {} ...", id);
            create(order);
            log.info("Successfully saved order for cart {} ", id);
            if (!createOrderDTO.isBuyNow()) {
                log.info("Deleting cart {} ...", id);
                cartRestService.deleteCart(id);
                log.info("Cart deleted successfully {}", id);
            }
        } else {
            throw new EcomException(HttpStatus.NOT_FOUND, "");
        }
    }

    private List<Product> getProducts(CreateOrderDTO createOrderDTO) {
        List<Product> products = null;
        if (createOrderDTO.isBuyNow()) {
            log.info("Getting product details for cart {} ", createOrderDTO.getId());
            products = productRestService.getProducts(List.of(createOrderDTO.getId()));
            log.info("Product details retrieved successfully for cart {} ", createOrderDTO.getId());
        } else {
            // Get cart details from cart service
            Cart cart = cartRestService.getCart();
            products = cart.getProducts();
        }
        return products;
    }

    @Override
    public List<OrderDTO> getOrders() {
        log.info("Fetching orders ...");
        List<Order> orderProducts = orderRepository.findByUserId("pranay1@gmail.com");
        List<Product> products = orderProducts.stream().flatMap(order -> order.getProducts().stream()).collect(Collectors.toList());
        List<OrderDTO> orderDTOS = new ArrayList<>(products.size());
        products.forEach(product -> {
            orderDTOS.add(OrderDTO.builder().productId(product.getProductId()).productName(product.getName()).image(product.getImage()).deliveryDate(LocalDate.now()).price(product.getDiscountedPrice()).build());
        });
        return orderDTOS;
    }
}
