package com.example.demo_blog.Controller;

import com.example.demo_blog.dto.CommentsDto;
import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.model.AppRole;
import com.example.demo_blog.model.Comments;
import com.example.demo_blog.model.Posts;
import com.example.demo_blog.model.Role;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.CommentsRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String token;
    private Users testUser;

    @BeforeEach
    public void setup() {
        // Clear all repositories
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Ensure Role is managed
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

        // Create and persist the user with the managed role
        testUser = new Users("uniqueuser", "user@example.com", encoder.encode("password1234"));
        testUser.addRole(userRole); // Synchronize bidirectional relationship
        testUser = userRepository.save(testUser);

        // Generate JWT token for the test user
        token = jwtUtils.generateTokenFromUsername(
                new User(testUser.getUserName(), testUser.getPassword(), Collections.emptyList())
        );
    }

    @Test
    public void testCreateComment_Success() throws Exception {
        // Create and persist a post
        Posts post = new Posts("Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), testUser);
        post = postsRepository.save(post);

        // Create a DTO for the comment
        CommentsDto commentsDto = new CommentsDto("Test Comment", post.getId());

        // Perform the API call to create a comment
        mockMvc.perform(post("/POST /comments")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentsDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test Comment"))
                .andExpect(jsonPath("$.postId").value(post.getId()));
    }

    @Test
    public void testGetAllCommentsByPostId_Success() throws Exception {
        // Create and persist a post
        Posts post = new Posts("Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), testUser);
        post = postsRepository.save(post);

        // Create and persist a comment
        Comments comment = new Comments("Test Comment", LocalDateTime.now(), post, testUser);
        commentsRepository.save(comment);

        // Perform the API call to fetch comments by post ID
        mockMvc.perform(get("/GET/comments")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("post_id", String.valueOf(post.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test Comment"))
                .andExpect(jsonPath("$[0].postId").value(post.getId()));
    }

    @Test
    public void testGetCommentById_Success() throws Exception {
        // Create and persist a post
        Posts post = new Posts("Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), testUser);
        post = postsRepository.save(post);

        // Create and persist a comment
        Comments comment = new Comments("Test Comment", LocalDateTime.now(), post, testUser);
        comment = commentsRepository.save(comment);

        // Perform the API call to fetch a comment by its ID
        mockMvc.perform(get("/GET/comments/" + comment.getId())
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test Comment"))
                .andExpect(jsonPath("$.postId").value(post.getId()));
    }
}
