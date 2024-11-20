package com.example.demo_blog.Controller;

import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.jwt.request.LoginRequest;
import com.example.demo_blog.jwt.request.SignUpRequest;
import com.example.demo_blog.model.AppRole;
import com.example.demo_blog.model.Role;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.RoleRepository;
import com.example.demo_blog.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = new Role(AppRole.ROLE_USER);
        roleRepository.save(userRole);
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        // Ensure the role is saved and managed by the repository
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseGet(() -> {
            Role newRole = new Role(AppRole.ROLE_USER);
            return roleRepository.save(newRole);
        });

        // Create a new user and assign the managed role
        Users user = new Users("user", "user@example.com", encoder.encode("password"));
        user.setRoles(Collections.singleton(userRole)); // Attach the managed role
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("user", "password");

        mockMvc.perform(post("/Post/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }


    @Test
    public void testRegisterUser_Success() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("uniqueuser3", "user3@example.com", "password1234", Collections.singleton("ROLE_USER"));

        mockMvc.perform(post("/POST/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    public void testRegisterUser_UsernameTaken() throws Exception {
        Users user = new Users("user", "user@example.com", encoder.encode("password"));
        userRepository.save(user);

        SignUpRequest signUpRequest = new SignUpRequest("user", "newuser@example.com", "password1234", Collections.singleton("ROLE_USER"));

        mockMvc.perform(post("/POST/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    public void testRegisterUser_EmailTaken() throws Exception {
        // Create a user with a different username but the same email
        Users user = new Users("differentuser", "user@example.com", encoder.encode("password"));
        userRepository.save(user);

        // Attempt to register a new user with the same email but different username
        SignUpRequest signUpRequest = new SignUpRequest("newuser", "user@example.com", "password1234", Collections.singleton("ROLE_USER"));

        mockMvc.perform(post("/POST/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

}
