package com.ecom.payment.controller;

import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.dto.PaymentResponse;
import com.ecom.payment.service.specification.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("pay")
    public PaymentResponse pay(@RequestBody CreatePaymentRequest paymentRequest) {
        return paymentService.pay(paymentRequest);
    }
}
