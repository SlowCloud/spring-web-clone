package com.web.clone.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    List<Posts> findAllByOrderByIdDesc();
}
