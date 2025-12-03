package com.ecom.order.service.specification;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    String createOrder(CreateOrderDTO createOrderDTO);

    void createOrderByEvent(ExecutionMessage executionMessage);
    void undoCreateOrder(String orderId);
    void undoCreateOrderByEvent(ExecutionMessage executionMessage);

    void validateOrderByEvent(ExecutionMessage executionMessage);

    void undoValidateOrderByEvent(ExecutionMessage executionMessage);
    List<OrderDTO> getOrders();

    void startOrderProcessing(CreateOrderDTO createOrderDTO);

}
