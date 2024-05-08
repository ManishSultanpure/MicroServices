package com.manish.OrderService.service;

import com.manish.OrderService.model.OrderRequest;
import com.manish.OrderService.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrder(String orderId);
}
