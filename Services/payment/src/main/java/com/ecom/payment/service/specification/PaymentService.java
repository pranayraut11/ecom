package com.ecom.payment.service.specification;

import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(CreatePaymentRequest createPaymentRequest);
}
