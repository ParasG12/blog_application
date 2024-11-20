package com.example.demo_blog.exception;

public class ApiException extends RuntimeException{
    public ApiException() {
    }
    public ApiException(String message) {
        super(message);
    }
}
