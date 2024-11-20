package com.example.demo_blog.jwt.request;

import java.util.Set;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
@NotNull
    private String username;

@NotNull
@Email
    private String email;

    private Set<String> role;

@NotNull
@Size(min=10)
    private String password;

    public Set<String> getRole() {
        return this.role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }
   public SignUpRequest(String username, String email,  String password,Set<String> role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
   }
}
