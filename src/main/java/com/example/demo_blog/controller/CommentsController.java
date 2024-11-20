package com.example.demo_blog.controller;

import com.example.demo_blog.dto.CommentsDto;
import com.example.demo_blog.dto.CommentsResponse;
import com.example.demo_blog.service.CommentsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class CommentsController {
    @Autowired
    CommentsService commentsService;
    @PostMapping("/POST/comments")
    public ResponseEntity<?> createComment(HttpServletRequest request, @RequestBody @Valid CommentsDto commentsDto){
   return new ResponseEntity<>(commentsService.save(request,commentsDto),HttpStatus.CREATED);
    }
    @GetMapping("/GET/comments")
    public ResponseEntity<?> getAllCommentsByPostId(HttpServletRequest request,
                                                    @RequestParam(name = "post_id", required = true) Long postId) {
        return new ResponseEntity<>(commentsService.getAllCommentsByPostId(request, postId), HttpStatus.OK);
    }
    @GetMapping("/GET/comments/{id}")
    public ResponseEntity<?> getCommentById(HttpServletRequest request,
                                           @PathVariable("id")Long id) {
        return new ResponseEntity<>(commentsService.getCommentById(id), HttpStatus.OK);
    }
    @PutMapping("/PUT /comments/{id}")
    public ResponseEntity<?>updateComment(HttpServletRequest request,@PathVariable("id")Long commentsId,@RequestBody CommentsDto commentsDto){
        return new ResponseEntity<>(commentsService.updateCommentById(request,commentsDto,commentsId),HttpStatus.OK);
    }
    @DeleteMapping("/DELETE /comments/{id}")
    public ResponseEntity<?>deleteComment(HttpServletRequest request,@PathVariable("id")Long commentsId){
        return new ResponseEntity<>(commentsService.deleteComment(request,commentsId),HttpStatus.OK);
    }



}


