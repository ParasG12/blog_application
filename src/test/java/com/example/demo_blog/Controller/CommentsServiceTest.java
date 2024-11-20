package com.example.demo_blog.Controller;

import com.example.demo_blog.dto.CommentsDto;
import com.example.demo_blog.dto.CommentsResponse;
import com.example.demo_blog.exception.ApiException;
import com.example.demo_blog.exception.ResourceNotFoundException;
import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.model.Comments;
import com.example.demo_blog.model.Posts;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.CommentsRepository;
import com.example.demo_blog.repository.PostsRepository;
import com.example.demo_blog.repository.UsersRepository;
import com.example.demo_blog.service.CommentsService;
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

public class CommentsServiceTest {

    @InjectMocks
    private CommentsService commentsService;

    @Mock
    private CommentsRepository commentsRepository;

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
    public void testSaveComment() {
        String token = "Bearer testtoken";
        String username = "testuser";
        Users user = new Users();
      user.setUserName(username);

        Posts post = new Posts();
        post.setId(1L);
        post.setTitle("Test Post");

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUserNameFromJwtToken(token.substring(7))).thenReturn(username);
        when(usersRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));

        CommentsDto commentsDto = new CommentsDto("Test Comment", 1L);
        Comments comments = new Comments();
        comments.setContent(commentsDto.getContent());
        comments.setPost(post);
        comments.setUser(user);
        comments.setCreatedAt(LocalDateTime.now());

        when(commentsRepository.save(any(Comments.class))).thenReturn(comments);

        CommentsResponse result = commentsService.save(request, commentsDto);

        assertEquals("Test Comment", result.getContent());
        assertEquals("testuser", result.getAuthorName());
    }

    @Test
    public void testGetAllCommentsByPostId() {
        String token = "Bearer testtoken";
        String username = "testuser";
        Users user = new Users();
        user.setUserName(username);

        Posts post = new Posts();
        post.setId(1L);
        post.setTitle("Test Post");

        Comments comment = new Comments();
        comment.setContent("Test Comment");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUserNameFromJwtToken(token.substring(7))).thenReturn(username);
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentsRepository.findByPost(post)).thenReturn(Arrays.asList(comment));

        List<CommentsResponse> result = commentsService.getAllCommentsByPostId(request, 1L);

        assertEquals(1, result.size());
        assertEquals("Test Comment", result.get(0).getContent());
    }

    @Test
    public void testUpdateCommentById() {
        String token = "Bearer testtoken";
        String username = "testuser";
        Users user = new Users();
        user.setUserName(username);

        Posts post = new Posts();
        post.setId(1L);
        post.setTitle("Test Post");

        Comments comment = new Comments();
        comment.setContent("Test Comment");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUserNameFromJwtToken(token.substring(7))).thenReturn(username);
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentsDto updatedCommentDto = new CommentsDto("Updated Comment", 1L);
        Comments updatedComment = new Comments();
        updatedComment.setContent(updatedCommentDto.getContent());
        updatedComment.setPost(post);
        updatedComment.setUser(user);
        updatedComment.setCreatedAt(LocalDateTime.now());

        when(commentsRepository.save(any(Comments.class))).thenReturn(updatedComment);

        CommentsResponse result = commentsService.updateCommentById(request, updatedCommentDto, 1L);

        assertEquals("Updated Comment", result.getContent());
    }

    @Test
    public void testDeleteCommentById() {
        Comments comment = new Comments();
        comment.setId(1L);
        comment.setContent("Test Comment");

        when(commentsRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comments result = commentsService.deleteComment(request, 1L);

        assertEquals("Test Comment", result.getContent());
        verify(commentsRepository, times(1)).delete(comment);
    }

    @Test
    public void testGetCommentById() {
        Users user = new Users();
        user.setUserName("testuser");

        Posts post = new Posts();
        post.setId(1L);
        post.setTitle("Test Post");

        Comments comment = new Comments();
        comment.setContent("Test Comment");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        when(commentsRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentsResponse result = commentsService.getCommentById(1L);

        assertEquals("Test Comment", result.getContent());
    }
}
