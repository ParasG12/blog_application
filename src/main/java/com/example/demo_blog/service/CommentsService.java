package com.example.demo_blog.service;

import com.example.demo_blog.dto.CommentsDto;
import com.example.demo_blog.dto.CommentsResponse;
import com.example.demo_blog.dto.PostResponse;
import com.example.demo_blog.exception.ApiException;
import com.example.demo_blog.exception.ResourceNotFoundException;
import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.model.Comments;
import com.example.demo_blog.model.Posts;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.CommentsRepository;
import com.example.demo_blog.repository.PostsRepository;
import com.example.demo_blog.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentsService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UsersRepository usersRepository;
    public String userNameFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        return jwtUtils.getUserNameFromJwtToken(token);

    }
    public CommentsResponse save(HttpServletRequest request, CommentsDto comment) {
        Posts post=postsRepository.findById(comment.getPostId()).orElseThrow(()->new ResourceNotFoundException(" post not found with "," postId ",comment.getPostId()));
        String userName=userNameFromToken(request);
        Users u= usersRepository.findByUserName(userName).get();
        Comments comments=new Comments();
        comments.setPost(post);
        comments.setContent(comment.getContent());
        comments.setCreatedAt(LocalDateTime.now());
        comments.setUser(u);
        Comments c=commentsRepository.save(comments);
       CommentsResponse response=new CommentsResponse();
       response.setContent(c.getContent());
       response.setCreatedAt(c.getCreatedAt());
       response.setPostId(c.getPost().getId());
       response.setAuthorName(userName);
       response.setPostTitle(c.getPost().getTitle());
       return response;

    }
    public List<CommentsResponse> getAllCommentsByPostId(HttpServletRequest request,Long postId){
       Posts p= postsRepository.findById(postId).orElseThrow(()->new ResourceNotFoundException(" post not found with "," postId ",postId));
        String userName=userNameFromToken(request);
        List<Comments>comments=commentsRepository.findByPost(p);
        if(comments.isEmpty()){
            throw new ApiException("empty comments list!!!");
        }
        List<CommentsResponse>commentsResponse=comments.stream().map(c->new CommentsResponse(c.getContent(),c.getCreatedAt(),c.getUser().getUserName(),c.getPost().getId(),c.getPost().getTitle())).collect(Collectors.toList());
        return commentsResponse;

    }
    public CommentsResponse updateCommentById(HttpServletRequest request, CommentsDto comment,Long commentId) {
        postsRepository.findById(comment.getPostId()).orElseThrow(()->new ResourceNotFoundException(" post not found with "," postId ",comment.getPostId()));
       Comments c= commentsRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException(" comment not found with "," commentId ",commentId));
       c.setContent(comment.getContent());
      c= commentsRepository.save(c);
      CommentsResponse response=new CommentsResponse();
      response.setContent(c.getContent());
      response.setCreatedAt(c.getCreatedAt());
      response.setPostTitle(c.getPost().getTitle());
      response.setAuthorName(userNameFromToken(request));
      response.setPostId(c.getPost().getId());
      return response;

    }
    public Comments deleteComment(HttpServletRequest request, Long commentId) {
        Comments c= commentsRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException(" comment not found with "," commentId ",commentId));
        commentsRepository.delete(c);
        return c;
    }
    public CommentsResponse getCommentById(Long id){
        CommentsResponse commentsResponse=new CommentsResponse();
        Comments c=commentsRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(" comment not found with "," commentId ",id));
        commentsResponse.setContent(c.getContent());
        commentsResponse.setCreatedAt(c.getCreatedAt());
        commentsResponse.setPostId(c.getPost().getId());
        commentsResponse.setAuthorName(c.getUser().getUserName());
        commentsResponse.setPostTitle(c.getPost().getTitle());
        return commentsResponse;


    }

}
