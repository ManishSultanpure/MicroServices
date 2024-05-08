package com.manish.PaymentService.service;

import com.manish.PaymentService.entity.TransactionDetails;
import com.manish.PaymentService.modal.PaymentMode;
import com.manish.PaymentService.modal.PaymentRequest;
import com.manish.PaymentService.modal.PaymentResponse;
import com.manish.PaymentService.repository.TransectionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private TransectionDetailsRepository transectionDetailsRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording Payment Details: {}", paymentRequest);
        TransactionDetails transactionDetails =
                TransactionDetails.builder().paymentDate(Instant.now()).
                        paymentMode(paymentRequest.getPaymentMode().name()).
                        paymentStatus("SUCCESS").
                        orderId(paymentRequest.getOrderId()).
                        referenceNumber(paymentRequest.getReferenceNumber()).
                        amount(paymentRequest.getAmount()).build();
        transectionDetailsRepository.save(transactionDetails);
        log.info("Trasection completed with the Id: {}", transactionDetails);
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentdetailsByOrderId(String orderId) {
        log.info("Getting Payment details by OrderId: {}" + orderId);
        TransactionDetails transactionDetails = transectionDetailsRepository.findByOrderId(Long.parseLong(orderId));
        PaymentResponse paymentResponse = PaymentResponse.builder().
                paymentId(transactionDetails.getId()).
                paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode())).
                paymentDate(transactionDetails.getPaymentDate()).
                orderId(transactionDetails.getOrderId()).
                status(transactionDetails.getPaymentStatus()).
                amount(transactionDetails.getAmount()).build();

        return paymentResponse;
    }
}
