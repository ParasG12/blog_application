package com.example.demo_blog.repository;

import com.example.demo_blog.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByEmail(String email);

Optional<Users> findByUserName(String username);

    boolean existsByUserName(String user1);

    boolean existsByEmail(String email);
}
