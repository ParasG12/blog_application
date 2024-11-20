package com.example.demo_blog.Controller;

import com.example.demo_blog.dto.PostDto;
import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.model.AppRole;
import com.example.demo_blog.model.Posts;
import com.example.demo_blog.model.Role;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.PostsRepository;
import com.example.demo_blog.repository.RoleRepository;
import com.example.demo_blog.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String token;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        postsRepository.deleteAll();

        Role userRole = new Role(AppRole.ROLE_USER);
        roleRepository.save(userRole);

        Users user = new Users("uniqueuser", "user@example.com", encoder.encode("password1234"));
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
        UserDetails userDetails = User.withUsername(user.getUserName()) .password(user.getPassword()) .authorities(userRole.getRoleName().name()) .build();


        token = jwtUtils.generateTokenFromUsername(userDetails);
    }

    @Test
    public void testCreatePost_Success() throws Exception {
        PostDto postDto = new PostDto("Test Title", "Test Content", "uniqueuser");

        MvcResult result = mockMvc.perform(post("/Post/posts")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postDto)))
                .andReturn();

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());

        mockMvc.perform(post("/Post/posts")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    public void testReadPost_Success() throws Exception {
        Posts post = new Posts("Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), userRepository.findByUserName("uniqueuser").get());
        postsRepository.save(post);

        MvcResult result = mockMvc.perform(get("/Post/posts")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());

        mockMvc.perform(get("/Post/posts")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].content").value("Test Content"));
    }

    @Test
    public void testReadPostById_Success() throws Exception {
        Posts post = new Posts("Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), userRepository.findByUserName("uniqueuser").get());
        Posts savedPost = postsRepository.save(post);

        MvcResult result = mockMvc.perform(get("/Post/posts/" + savedPost.getId())
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());

        mockMvc.perform(get("/Post/posts/" + savedPost.getId())
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    public void testUpdatePost_Success() throws Exception {
        Posts post = new Posts("Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), userRepository.findByUserName("uniqueuser").get());
        Posts savedPost = postsRepository.save(post);

        PostDto updatedPostDto = new PostDto("Updated Title", "Updated Content", "uniqueuser");

        MvcResult result = mockMvc.perform(put("/Post/posts/" + savedPost.getId())
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedPostDto)))
                .andReturn();

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());

        mockMvc.perform(put("/Post/posts/" + savedPost.getId())
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    public void testDeletePost_Success() throws Exception {
        Posts post = new Posts("Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), userRepository.findByUserName("uniqueuser").get());
        Posts savedPost = postsRepository.save(post);

        MvcResult result = mockMvc.perform(delete("/Post/posts/" + savedPost.getId())
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());

        mockMvc.perform(delete("/Post/posts/" + savedPost.getId())
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
