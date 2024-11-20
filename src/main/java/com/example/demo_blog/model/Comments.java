package com.example.demo_blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    @ManyToOne()
    @JoinColumn(name="post_id")
    private Posts post;
    @ManyToOne()
    @JoinColumn(name="author_id")
    private Users user;


    public Comments(String testComment, LocalDateTime now, Posts post, Users uniqueuser) {
        this.content = testComment;
        this.createdAt = now;
        this.post = post;
        this.user = uniqueuser;
    }
}
//`comments`: id, post_id, content, author_id, created_at

