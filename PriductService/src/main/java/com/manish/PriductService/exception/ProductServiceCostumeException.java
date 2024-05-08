package com.manish.PriductService.exception;

import lombok.Data;

@Data
public class ProductServiceCostumeException extends RuntimeException{
    private String errorCode;
    public ProductServiceCostumeException(String message,String errorCode){
        super(message);
        this.errorCode=errorCode;
    }


}
