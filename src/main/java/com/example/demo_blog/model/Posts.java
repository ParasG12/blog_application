package com.example.demo_blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Posts(Long id) {
        this.id = id;
    }

    public Posts(String testTitle, String testContent, LocalDateTime now, LocalDateTime now1, Users uniqueuser) {
        this.title = testTitle;
        this.content = testContent;
        this.createdAt = now;
        this.updatedAt = now1;
        this.author = uniqueuser;
    }


    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @ManyToOne()
    @JoinColumn(name="author_id")
    private Users author;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comments> comments=new ArrayList<>();
}




