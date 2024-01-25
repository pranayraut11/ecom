package com.ecom.payment.mappers.specification;

import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.entity.TransactionEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-01T00:59:39+0530",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
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
