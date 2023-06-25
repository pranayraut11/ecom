package com.ecom.payment.mappers.specification;

import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.entity.TransactionEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-25T15:19:43+0530",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.18 (Ubuntu)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public TransactionEntity paymentDTOToPayment(CreatePaymentRequest productDTO) {
        if ( productDTO == null ) {
            return null;
        }

        TransactionEntity transactionEntity = new TransactionEntity();

        transactionEntity.setOrderId( productDTO.getOrderId() );
        transactionEntity.setPaymentMode( productDTO.getPaymentMode() );
        transactionEntity.setPaymentServiceProvider( productDTO.getPaymentServiceProvider() );
        transactionEntity.setAmount( productDTO.getAmount() );

        return transactionEntity;
    }
}
