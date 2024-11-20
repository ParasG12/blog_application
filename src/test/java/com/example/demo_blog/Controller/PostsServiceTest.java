package com.example.demo_blog.Controller;

import com.example.demo_blog.dto.PostDto;
import com.example.demo_blog.exception.ApiException;
import com.example.demo_blog.exception.ResourceNotFoundException;
import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.model.Posts;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.PostsRepository;
import com.example.demo_blog.repository.UsersRepository;
import com.example.demo_blog.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostsServiceTest {

    @InjectMocks
    private PostsService postsService;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSavePost() {
        String token = "Bearer testtoken";
        String username = "testuser";
        Users user = new Users();
        user.setUserName(username);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUserNameFromJwtToken(token.substring(7))).thenReturn(username);
        when(usersRepository.findByUserName(username)).thenReturn(Optional.of(user));

        PostDto postDto = new PostDto("Test Title", "Test Content", username);
        Posts post = new Posts();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setAuthor(user);
        post.setCreatedAt(LocalDateTime.now());

        postsService.savePost(request, postDto);

        verify(postsRepository, times(1)).save(any(Posts.class));
    }

    @Test
    public void testReadPost() {
        String token = "Bearer testtoken";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUserNameFromJwtToken(token.substring(7))).thenReturn(username);

        Users user = new Users();
        user.setUserName(username);
        Posts post = new Posts(1L, "Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), user, null);
        List<Posts> postList = Arrays.asList(post);

        when(postsRepository.findAll()).thenReturn(postList);

        List<PostDto> result = postsService.readPost(request);

        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    public void testReadPostById() {
        String token = "Bearer testtoken";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUserNameFromJwtToken(token.substring(7))).thenReturn(username);

        Users user = new Users();
      user.setUserName(username);
        Posts post = new Posts(1L, "Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), user, null);
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));

        PostDto result = postsService.readPostById(request, 1L);

        assertEquals("Test Title", result.getTitle());
    }

    @Test
    public void testUpdatePost() {
        String token = "Bearer testtoken";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUserNameFromJwtToken(token.substring(7))).thenReturn(username);

        Users user = new Users();
        user.setUserName(username);
        Posts post = new Posts(1L, "Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), user, null);
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));

        PostDto updatedPostDto = new PostDto("Updated Title", "Updated Content", username);
        when(modelMapper.map(any(Posts.class), eq(PostDto.class))).thenReturn(updatedPostDto);

        PostDto result = postsService.updatePost(request, updatedPostDto, 1L);

        System.out.println("Result title: " + result.getTitle());

        assertEquals("Updated Title", result.getTitle());
    }


    @Test
    public void testDeletePostById() {
        Posts post = new Posts(1L, "Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), null, null);
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));

        postsService.deletePostById(request, 1L);

        verify(postsRepository, times(1)).delete(post);
    }
}
