package com.ecom.payment.service.implementation;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import com.ecom.payment.constants.enums.PaymentStatus;
import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.dto.PaymentResponse;
import com.ecom.payment.entity.TransactionEntity;
import com.ecom.payment.mappers.specification.PaymentMapper;
import com.ecom.payment.repository.PaymentRepository;
import com.ecom.payment.service.specification.PaymentService;
import com.ecom.shared.common.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PaymentServiceImpl  extends BaseService<TransactionEntity> implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrchestrationService orchestrationService;

    @Override
    public PaymentResponse pay(CreatePaymentRequest createPaymentRequest) {
        log.info("Processing payment for order ID: {}", createPaymentRequest.getOrderId());
        TransactionEntity entity = paymentMapper.paymentDTOToPayment(createPaymentRequest);
        paymentRepository.save(entity);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setTransactionId(entity.getId());
        paymentResponse.setOrderId(entity.getOrderId());
        paymentResponse.setStatus(PaymentStatus.PAYMENT_APPROVED);
        log.info("Payment processed successfully for order ID: {}", createPaymentRequest.getOrderId());
        return paymentResponse;
    }

    @Override
    public PaymentResponse refund(String transactionId) {
        log.info("Initiating refund for transaction ID: {}", transactionId);
        TransactionEntity entity = paymentRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        entity.setStatus(PaymentStatus.PAYMENT_REFUNDED);
        paymentRepository.save(entity);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setTransactionId(entity.getId());
        paymentResponse.setOrderId(entity.getOrderId());
        paymentResponse.setStatus(PaymentStatus.PAYMENT_REFUNDED);
        log.info("Refund completed for transaction ID: {}", transactionId);
        return paymentResponse;
    }

    @Override
    public void payByEvent(ExecutionMessage executionMessage) {
        log.info("Processing payment event: {}", executionMessage);
        orchestrationService.doNext(executionMessage);
    }

    @Override
    public void refundByEvent(ExecutionMessage executionMessage) {
        log.info("Processing refund event: {}", executionMessage);
        orchestrationService.undoNext(executionMessage);
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
