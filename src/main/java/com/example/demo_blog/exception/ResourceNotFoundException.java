package com.example.demo_blog.exception;

public class ResourceNotFoundException extends  RuntimeException{
    String resourceName;
    String fieldName;
    String field;
    Long fieldId;
    public ResourceNotFoundException(String resourceName,String field,String fieldName) {
        super(String.format("%s not found with %s: %s",resourceName,field,fieldName));
        this.resourceName=resourceName;
        this.field=field;
        this.fieldName=fieldName;

    }
    public ResourceNotFoundException(String resourceName,String field,long fieldId) {
        super(String.format("%s not found with %s: %d",resourceName,field,fieldId));
        this.resourceName=resourceName;
        this.field=field;
        this.fieldId=fieldId;

    }
    public ResourceNotFoundException(){

    }
}