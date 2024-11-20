package com.example.demo_blog.repository;

import com.example.demo_blog.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Dictionary;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {

}
