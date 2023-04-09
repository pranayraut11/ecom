package com.ecom.payment.repository;

import com.ecom.payment.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<TransactionEntity,String> {
}
