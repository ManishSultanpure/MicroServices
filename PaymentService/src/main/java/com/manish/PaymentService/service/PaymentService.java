package com.manish.PaymentService.service;

import com.manish.PaymentService.modal.PaymentRequest;
import com.manish.PaymentService.modal.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentdetailsByOrderId(String orderId);
}
