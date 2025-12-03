package com.ecom.order.service.implementation;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderDTO;
import com.ecom.order.dto.OrderProductInput;
import com.ecom.order.entity.Order;
import com.ecom.order.entity.OrderStatus;
import com.ecom.order.mapper.OrderMapper;
import com.ecom.order.model.Product;
import com.ecom.order.repository.OrderRepository;
import com.ecom.order.rest.OrchestratorClient;
import com.ecom.order.rest.ProductClient;
import com.ecom.order.service.specification.OrderService;
import com.ecom.order.util.OrderProductUtil;
import com.ecom.order.util.OrderValidationUtil;
import com.ecom.order.util.SnowflakeIdGenerator;
import com.ecom.order.util.UserContextUtil;
import com.ecom.shared.common.service.BaseService;
import com.ecom.shared.contract.dto.InventoryRequest;
import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import com.ecom.shared.contract.dto.PageRequestDTO;
import com.ecom.shared.contract.dto.SearchCriteria;
import com.ecom.shared.contract.enums.Operator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends BaseService<Order> implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    //@Autowired
    //private ProductClient productClients;

    @Autowired
    private OrchestrationService orchestrationService;


//    @Autowired
//    private OrderProducer orderProducer;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

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

    // Method made protected for testing
    protected String getUserIdFromRequest() {
        if (request == null) {
            // This is for testing only
            return "test-user-id";
        }
        return UserContextUtil.extractUserId(request);
    }

    @Override
    public String createOrder(CreateOrderDTO createOrderDTO) {
        log.info("Request received for creating order");
//        String userId = getUserIdFromRequest();
//        // Fetch products using utility
//        List<Product> foundProducts = null;//OrderProductUtil.fetchProductsForOrder(createOrderDTO, productClients);
//        // Delegate validation and mapping
//        List<Product> validatedProducts = OrderValidationUtil.validateAndMapProducts(createOrderDTO, foundProducts);
//        Order order = orderMapper.toOrder(createOrderDTO, validatedProducts);
//        order.setOrderId(String.valueOf(snowflakeIdGenerator.nextId()));
//        order.setUserId(userId);
//        order.setStatus(OrderStatus.CREATED);
//        orderRepository.save(order);
        startOrderProcessing(createOrderDTO);
        log.info("Order created successfully with orderId: {}", "order.getOrderId()");
        return "order.getOrderId()";
    }

    @Override
    public void createOrderByEvent(ExecutionMessage executionMessage) {
        log.info("Creating order by event ...");
        CreateOrderDTO requestDTO = objectMapper.convertValue(executionMessage.getPayload(), CreateOrderDTO.class);
        log.info("Order creation request received for addressId: {}", requestDTO.getAddressId());
        orchestrationService.doNext(executionMessage);
    }

    @Override
    public void undoCreateOrder(String orderId) {
        log.info("Undoing order creation for orderId: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found for undo operation"));
        orderRepository.delete(order);
        log.info("Order with orderId: {} has been deleted as part of undo operation", orderId);
    }

    @Override
    public void undoCreateOrderByEvent(ExecutionMessage executionMessage) {
        log.info("Undoing order creation by event ...");
        orchestrationService.undoNext(executionMessage);
    }

    @Override
    public void validateOrderByEvent(ExecutionMessage executionMessage) {
        log.info("Validating order by event ...");
        CreateOrderDTO requestDTO = objectMapper.convertValue(executionMessage.getPayload(), CreateOrderDTO.class);
        log.info("Order validation request received for orderId: {}", requestDTO.getAddressId());
        orchestrationService.doNext(executionMessage);
    }

    @Override
    public void undoValidateOrderByEvent(ExecutionMessage executionMessage) {
    log.info("Undoing order validation by event ...");
        orchestrationService.undoNext(executionMessage);
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

    @Override
    public void startOrderProcessing(CreateOrderDTO createOrderDTO) {
        ExecutionMessage executionMessage = new ExecutionMessage();
        executionMessage.setPayload(createOrderDTO);
        Map<String, Object> headers = new HashMap<>();
        headers.put("correlationId", UUID.randomUUID().toString());
        executionMessage.setHeaders(headers);
        orchestrationService.startOrchestration(executionMessage,"orderProcessing");
    }
}
