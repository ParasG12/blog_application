package com.example.demo_blog.dto;

import lombok.*;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsResponse {
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
    private Long postId;
    private String postTitle;
}
