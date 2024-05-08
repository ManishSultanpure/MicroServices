package com.manish.PaymentService.repository;

import com.manish.PaymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransectionDetailsRepository extends JpaRepository<TransactionDetails,Long> {
TransactionDetails findByOrderId(Long orderId);
}
