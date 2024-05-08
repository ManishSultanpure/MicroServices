package com.manish.PriductService.service;

import com.manish.PriductService.entity.Product;
import com.manish.PriductService.exception.ProductServiceCostumeException;
import com.manish.PriductService.model.ProductRequest;
import com.manish.PriductService.model.ProductResponse;
import com.manish.PriductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("Adding Product");
        Product product=Product.builder().
                name(productRequest.getName()).
        quantity(productRequest.getQuantity()).
        price(productRequest.getPrice()).build();
        log.info("Product Created"+product);
         productRepository.save(product);
        return product.getId();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        log.info("Product Id" +productId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ProductServiceCostumeException("Product Not available for this id","PRODUCT_NOT_FOUND")
        );

        ProductResponse productResponse=new ProductResponse();
        copyProperties(product,productResponse);
    return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quanity) {
    log.info("Reduce Quantity {} for Id: {}",productId);
    Product product
            =productRepository.findById(productId).orElseThrow(()->new ProductServiceCostumeException("product with given Id not found","PRODUCT_NOT_FOUND"));
    if(product.getQuantity()<quanity){
        throw new ProductServiceCostumeException(
                "product does not have sufficent quantity",
                "INSUFFICENT_QUANTITY"
        );
    }
    product.setQuantity(product.getQuantity()-quanity);
    productRepository.save(product);
    log.info("product Quantity updated successfully");
    }
}
