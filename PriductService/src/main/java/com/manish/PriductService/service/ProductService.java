package com.manish.PriductService.service;

import com.manish.PriductService.model.ProductRequest;
import com.manish.PriductService.model.ProductResponse;

public interface ProductService {
    Long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long productId);

    void reduceQuantity(long productId, long quanity);
}
