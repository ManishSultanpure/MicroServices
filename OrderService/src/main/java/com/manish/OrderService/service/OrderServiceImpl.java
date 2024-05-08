package com.manish.OrderService.service;

import com.manish.OrderService.exception.CustomException;
import com.manish.OrderService.external.client.PaymentService;
import com.manish.OrderService.external.request.PaymentRequest;
import com.manish.OrderService.external.response.PaymentResponse;
import com.manish.OrderService.model.OrderResponse;
import com.manish.OrderService.repository.OrderRepository;
import com.manish.OrderService.entity.Order;
import com.manish.OrderService.external.client.ProductService;
import com.manish.OrderService.model.OrderRequest;
import com.manish.PriductService.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {

//        Order Entity->save the data with status order created
//        ProductService -> Block Product(reduce the Quantity)
//        Payment Service-> payments-> Success->COMPLETE,Else CANCLLED

        log.info("Order Request {} ", orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.info("Creating order with the status CREATED");
        Order order = Order.builder().
                amount(orderRequest.getTotleAmount()).
                orderStatus("CREATED").
                productId(orderRequest.getProductId()).
                orderDate(Instant.now()).
                quantity(orderRequest.getQuantity()).build();

        order = orderRepository.save(order);

        log.info("Calling Payment service to complete the payment");
        PaymentRequest paymentRequest =
                PaymentRequest.builder().
                        orderId(order.getId()).
                        paymentMode(orderRequest.getPaymentMode()).
                        amount(orderRequest.getTotleAmount())
                        .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully. Changing the order status to placed");
            orderStatus = "PLACED";
        } catch (Exception exception) {
            log.error("Error occured in payment. Changing order status to FAILED ");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order Placed Id {} ", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrder(String orderId) {
        log.info("Get order details for orderId : {}", orderId);
        Order order
                = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new CustomException("Order not found from this order id: {}" + orderId, "NOT_FOUND", 404));

        log.info("Invoking the Product  servuce to fetch the product details ");
        ProductResponse productResponse =
                restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
                        ProductResponse.class);

        log.info("getting payment information from the payment service");
        PaymentResponse paymentResponse = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class);

        OrderResponse.ProductDetails productDetails =
                OrderResponse.ProductDetails.builder().
                        name(productResponse.getName()).
                        id(productResponse.getId())
                        .build();

        OrderResponse.PaymentDetails paymentDetails =
                OrderResponse.PaymentDetails.builder().
                        paymentId(paymentResponse.getPaymentId()).
                        paymentStatus(paymentResponse.getStatus()).
                        paymentDate(paymentResponse.getPaymentDate()).
                        paymentMode(paymentResponse.getPaymentMode()).build();

        OrderResponse orderResponse =
                OrderResponse.builder().
                        orderId(order.getId()).
                        orderStatus(order.getOrderStatus()).
                        amount(order.getAmount()).
                        orderDate(order.getOrderDate()).
                        productDetails(productDetails).
                        paymentDetails(paymentDetails)
                        .build();
        return orderResponse;
    }
}
