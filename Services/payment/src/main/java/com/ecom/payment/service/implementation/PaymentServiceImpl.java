package com.ecom.payment.service.implementation;

import com.ecom.payment.constants.enums.PaymentStatus;
import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.dto.PaymentResponse;
import com.ecom.payment.entity.TransactionEntity;
import com.ecom.payment.mappers.specification.PaymentMapper;
import com.ecom.payment.repository.PaymentRepository;
import com.ecom.payment.service.specification.PaymentService;
import com.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl  extends BaseService<TransactionEntity> implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public PaymentResponse pay(CreatePaymentRequest createPaymentRequest) {
        TransactionEntity entity = paymentMapper.paymentDTOToPayment(createPaymentRequest);
        paymentRepository.save(entity);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setTransactionId(entity.getId());
        paymentResponse.setOrderId(entity.getOrderId());
        paymentResponse.setStatus(PaymentStatus.PAYMENT_APPROVED);
        return paymentResponse;
    }

    @Override
    public List<TransactionEntity> getAll() {
        return null;
    }

    @Override
    public TransactionEntity get(String id) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public TransactionEntity create(TransactionEntity entity) {
        return null;
    }

    @Override
    public TransactionEntity update(TransactionEntity entity) {
        return null;
    }
}
