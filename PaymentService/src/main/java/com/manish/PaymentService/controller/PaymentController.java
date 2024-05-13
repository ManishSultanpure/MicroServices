package com.manish.PaymentService.controller;

import com.manish.PaymentService.modal.PaymentRequest;
import com.manish.PaymentService.modal.PaymentResponse;
import com.manish.PaymentService.service.PaymentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@Log4j2
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest),
                HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable String orderId){
        log.info("Order id for Payment details: {} "+orderId);
        return new ResponseEntity<>(
                paymentService.getPaymentdetailsByOrderId(orderId),
                HttpStatus.OK
        );
    }
}
