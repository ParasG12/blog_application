package com.example.demo_blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter

@NoArgsConstructor
public class PostDto {
@NotNull
    private String title;
    @NotNull
    private String content;

    private String author;


    public PostDto(String title, String content, String userName) {
        this.title=title;
        this.content=content;
        this.author=userName;
    }
}
