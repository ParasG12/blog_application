package com.example.demo_blog.repository;

import com.example.demo_blog.model.Comments;
import com.example.demo_blog.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments,Long> {
   List<Comments> findByPost(Posts post);
}
