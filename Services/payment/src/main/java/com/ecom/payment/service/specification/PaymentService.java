package com.ecom.payment.service.specification;

import com.ecom.payment.dto.CreatePaymentRequest;

public interface PaymentService {
    void pay(CreatePaymentRequest createPaymentRequest);
}
