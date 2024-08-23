package com.ecom.order.service.implementation;

import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderDTO;
import com.ecom.order.entity.Order;
import com.ecom.order.model.Product;
import com.ecom.order.repository.OrderRepository;
import com.ecom.order.rest.OrchestratorClient;
import com.ecom.order.rest.ProductClient;
import com.ecom.order.service.specification.OrderService;
import com.ecom.shared.common.service.BaseService;
import com.ecom.shared.contract.dto.InventoryRequest;
import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import com.ecom.shared.contract.dto.PageRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ProductClient productClients;

    @Autowired
    private OrchestratorClient orchestratorClient;

//    @Autowired
//    private OrderProducer orderProducer;

    @Autowired
    private ObjectMapper objectMapper;

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
    public String createOrder(CreateOrderDTO createOrderDTO) {
        log.info("Request received for creating order");
        orchestratorClient.orchestrateOrder(OrderOrchestratorRequestDTO.builder().inventory(InventoryRequest.builder().products(List.of(com.ecom.shared.contract.dto.Product.builder().id("3453").quantity(3).build())).build()).orderId(UUID.randomUUID()).build());
        return "SUCCESS";
    }

    private List<Product> getProducts(CreateOrderDTO createOrderDTO) {
        List<Product> products = null;
        if (createOrderDTO.isBuyNow()) {
            PageRequestDTO pageRequestDTO = PageRequestDTO.builder().size(2).page(1).build().idCriteria(createOrderDTO.getId());
            log.info("Getting product details for product {} ", createOrderDTO.getId());
            products = productClients.getProducts(pageRequestDTO);
            log.info("Product details retrieved successfully {} ", createOrderDTO.getId());
        } else {
            // Get cart details from cart service
            //Cart cart = cartRestService.getCart();
           // products = cart.getProducts();
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
