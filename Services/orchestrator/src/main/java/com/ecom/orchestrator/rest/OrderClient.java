package com.ecom.orchestrator.rest;

import com.ecom.orchestrator.dto.CreateOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("order")
public interface OrderClient {

    @RequestMapping(method = RequestMethod.POST,value = "/order")
    String createOrder(CreateOrderDTO createOrderDTO);
}
