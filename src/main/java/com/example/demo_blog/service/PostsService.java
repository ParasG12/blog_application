package com.example.demo_blog.service;

import com.example.demo_blog.dto.PostDto;
import com.example.demo_blog.exception.ApiException;
import com.example.demo_blog.exception.ResourceNotFoundException;
import com.example.demo_blog.jwt.JwtUtils;
import com.example.demo_blog.model.Posts;
import com.example.demo_blog.model.Users;
import com.example.demo_blog.repository.PostsRepository;
import com.example.demo_blog.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostsService {
@Autowired
ModelMapper modelMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    UsersRepository usersRepository;
    public  String getUserNameFromToken(HttpServletRequest request){
        String token=request.getHeader("Authorization");
        token=token.substring(7);
        String userName=jwtUtils.getUserNameFromJwtToken(token);
        return userName;
    }
    public void  savePost(HttpServletRequest request,PostDto post) {
        String userName=getUserNameFromToken(request);
       Users u= usersRepository.findByUserName(userName).get();
       Posts posts=new Posts();
       System.out.println(post.getContent()+" hello "+post.getTitle());
       posts.setTitle(post.getTitle());
       posts.setContent(post.getContent());
       posts.setAuthor(u);
       posts.setCreatedAt(LocalDateTime.now());
       postsRepository.save(posts);
    }

    public  List<PostDto>readPost(HttpServletRequest request) {
        String userName=getUserNameFromToken(request);
        List<Posts>posts=postsRepository.findAll();
        if(posts.isEmpty()){
            throw  new ApiException("postList is empty");
        }
       return  posts.stream()
               .map(post -> new PostDto(post.getTitle(), post.getContent(), userName))
               .collect(Collectors.toList());
    }

    public PostDto readPostById(HttpServletRequest request, Long id) {
        Posts post=   postsRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(" post not found with "," postId ",id));
     return new PostDto(post.getTitle(), post.getContent(),post.getAuthor().getUserName());
    }
    public PostDto updatePost(HttpServletRequest request, PostDto post,Long id) {
        Posts posts= postsRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(" post not found with "," postId ",id));
       posts.setTitle(post.getTitle());
       posts.setContent(post.getContent());
       posts.setUpdatedAt(LocalDateTime.now());
       Posts p=postsRepository.save(posts);
       PostDto postDto=modelMapper.map(p,PostDto.class);
       return postDto;
    }
    public PostDto deletePostById(HttpServletRequest request,Long id){
        Posts posts= postsRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(" post not found with "," postId ",id));
        postsRepository.delete(posts);
        PostDto postDto=modelMapper.map(posts,PostDto.class);
        return postDto;
    }


}
