package com.example.demo_blog.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.example.demo_blog.controller.AuthController;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.jwt.request.LoginRequest;
import com.example.demo_blog.jwt.request.SignUpRequest;
import com.example.demo_blog.jwt.response.MessageResponse;
import com.example.demo_blog.jwt.services.UserDetailsImpl;
import com.example.demo_blog.jwt.services.UserDetailsServiceImpl;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.RoleRepository;
import com.example.demo_blog.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsersRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setup() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "user@gmail.com", "password", authorities);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("user", "user@example.com", "password", null);
        Users user = new Users("user", "user@example.com", "encodedPassword");
        Mockito.when(userRepository.existsByUserName(anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(false);
        Mockito.when(encoder.encode(anyString())).thenReturn("encodedPassword");
        mockMvc.perform(post("/Post/login").with(SecurityMockMvcRequestPostProcessors.csrf())); // Add CSRF token .contentType(MediaType.APPLICATION_JSON) .content(new ObjectMapper().writeValueAsString(signUpRequest))) .andExpect(status().isOk()) .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }



    @Test
    public void testAuthenticateUser_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "password");
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "user@gmail.com", "password", authorities);
        String jwtToken = "jwtToken";

        // Mock the authentication process to return the UserDetailsImpl object
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));

        // Mock the JWT generation to return the token for the UserDetails object
        Mockito.when(jwtUtils.generateTokenFromUsername(any(UserDetailsImpl.class))).thenReturn(jwtToken);

        mockMvc.perform(post("/Post/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.jwtToken").value(jwtToken)); // Updated JSON path key
    }



    @Test
    public void testAuthenticateUser_BadCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "wrongpassword");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        MvcResult result = mockMvc.perform(post("/Post/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andReturn();

        // Log the response status and content
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());

        // Verify the response
        mockMvc.perform(post("/Post/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()) // Expect 401 Unauthorized status for bad credentials
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }


    @Test
    public void testRegisterUser_UsernameTaken() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("user", "user@example.com", "password", null);

        Mockito.when(userRepository.existsByUserName(Mockito.anyString())).thenReturn(true);

        mockMvc.perform(post("/Post/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest()) // Expected status code 400
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }





    @Test
    public void testRegisterUser_EmailTaken() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("user", "user@example.com", "password", null);

        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        mockMvc.perform(post("/Post/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest()) // Expected status code 400
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }





}
