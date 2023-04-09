package com.ecom.payment.entity;

import com.ecom.payment.constants.enums.PaymentMode;
import com.ecom.payment.constants.enums.PaymentStatus;
import com.ecom.shared.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Data
@Document
public class TransactionEntity extends BaseEntity {

    private String orderId;
    private PaymentMode paymentMode;
    private String paymentServiceProvider;
    private BigInteger amount;
    private String transactionId;
    private PaymentStatus status;


}
