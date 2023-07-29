package com.driver.exception;

public class ProductionHouseDoesNotExists extends RuntimeException{
    public ProductionHouseDoesNotExists(String message) {
        super(message);
    }
}
