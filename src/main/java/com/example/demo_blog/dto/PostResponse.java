package com.example.demo_blog.dto;

import com.example.demo_blog.model.Users;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
public class PostResponse {
    private String title;
    private String content;

    private String userName;
}
