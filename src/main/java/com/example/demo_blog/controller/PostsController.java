package com.example.demo_blog.controller;

import com.example.demo_blog.dto.PostDto;
import com.example.demo_blog.model.Posts;
import com.example.demo_blog.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class PostsController {
    @Autowired
    private PostsService postsService;
    @PostMapping("/POST/posts")
    public ResponseEntity<?>savePost(HttpServletRequest request,@RequestBody  @Valid PostDto post){
        postsService.savePost(request,post);
        return new ResponseEntity<>("post created success", HttpStatus.CREATED);
    }
    @GetMapping("/GET/posts")
    public ResponseEntity<?>readPost(HttpServletRequest request){
        return new ResponseEntity<>(postsService.readPost(request), HttpStatus.OK);
    }
    @GetMapping("/GET/posts/{id}")
    public ResponseEntity<?>readPostById(HttpServletRequest request,@PathVariable("id") Long id){
        return new ResponseEntity<>(postsService.readPostById(request,id), HttpStatus.OK);
    }
    @PutMapping("/PUT/posts/{id}")
    public ResponseEntity<?>updatePostById(HttpServletRequest request,@PathVariable("id") Long id,@RequestBody @Valid PostDto post){
        return new ResponseEntity<>(postsService.updatePost(request,post,id), HttpStatus.OK);
    }
    @DeleteMapping("/DELETE/posts/{id}")
    public ResponseEntity<?>deletePost(HttpServletRequest request,@PathVariable("id") Long id){
        return new ResponseEntity<>(postsService.deletePostById(request,id), HttpStatus.OK);
    }

}
