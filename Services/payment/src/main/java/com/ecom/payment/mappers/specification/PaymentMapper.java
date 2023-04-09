package com.ecom.payment.mappers.specification;

import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", implementationPackage = "com.ecom.payment.mappers.specification")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper( PaymentMapper.class );
    TransactionEntity paymentDTOToPayment(CreatePaymentRequest productDTO);
}