package com.ecom.order.service.implementation;

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
import com.ecom.order.util.SnowflakeIdGenerator;
import com.ecom.order.util.UserContextUtil;
import com.ecom.shared.common.service.BaseService;
import com.ecom.shared.contract.dto.InventoryRequest;
import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import com.ecom.shared.contract.dto.PageRequestDTO;
import com.ecom.shared.contract.dto.SearchCriteria;
import com.ecom.shared.contract.enums.Operator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private HttpServletRequest request;

    private final SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1, 1); // configure workerId/datacenterId as needed

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
        String userId = UserContextUtil.extractUserId(request);
        List<String> productIds = createOrderDTO.getProducts().stream()
                .map(OrderProductInput::getProductId)
                .toList();
        List<SearchCriteria> inCriteria = new java.util.ArrayList<>();
        inCriteria.add(SearchCriteria.builder().key("_id").values(new java.util.ArrayList<>(productIds)).build());
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .inCriteria(inCriteria)
                .size(productIds.size())
                .page(1)
                .build();
        List<Product> foundProducts = productClients.getProducts(pageRequestDTO);
        // Build a map for fast lookup: key = productId + sellerId
        Map<String, Product> productMap = foundProducts.stream()
                .collect(Collectors.toMap(
                        p -> p.getProductId() + "::" + p.getSellerId(),
                        p -> p
                ));
        List<Product> validatedProducts = new java.util.ArrayList<>();
        for (OrderProductInput input : createOrderDTO.getProducts()) {
            String key = input.getProductId() + "::" + input.getSellerId();
            Product actual = productMap.get(key);
            if (actual == null) {
                throw new IllegalArgumentException("Product not found or not available for seller: " + key);
            }
            actual.setQuantity((short) input.getQuantity());
            actual.setSku(input.getSku());
            validatedProducts.add(actual);
        }
        Order order = orderMapper.toOrder(createOrderDTO, validatedProducts);
        order.setOrderId(String.valueOf(snowflakeIdGenerator.nextId()));
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return order.getOrderId();
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
