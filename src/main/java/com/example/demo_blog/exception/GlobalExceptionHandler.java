package com.example.demo_blog.exception;

import com.example.demo_blog.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException){
        Map<String,String> response=new HashMap<String,String>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(err->{
            String error=((FieldError)err).getField();
            String message=err.getDefaultMessage();
            response.put(error,message);

        });



        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);



    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse>myResourceNotFoundException(ResourceNotFoundException ex){
        String message=ex.getMessage();
        ApiResponse apiResponse=new ApiResponse(ex.getMessage(),false);


        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse>myApiException(ApiException ex){
        String message=ex.getMessage();
        ApiResponse apiResponse=new ApiResponse(ex.getMessage(),false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
}
