package com.application.custom_excs;

public class DonorNotFoundException extends RuntimeException {
    public DonorNotFoundException(String message) {
        super(message);
    }
}