package com.manish.PriductService.controller;

import com.manish.PriductService.entity.Product;
import com.manish.PriductService.model.ProductRequest;
import com.manish.PriductService.model.ProductResponse;
import com.manish.PriductService.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest){
        Long productId= productService.addProduct(productRequest);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }
    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority('SCOPE_internal')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long productId){
        ProductResponse productResponse= productService.getProductById(productId);
    return  new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @PutMapping("/reduceQuanity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quanity
    ){
        productService.reduceQuantity(productId,quanity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
