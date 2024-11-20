package com.example.demo_blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsDto {
    @NotNull
    private String content;
    @NotNull
    private Long postId;
}
