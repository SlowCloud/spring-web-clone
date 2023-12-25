package com.web.clone.service;

import com.web.clone.domain.posts.Posts;
import com.web.clone.domain.posts.PostsRepository;
import com.web.clone.web.dto.PostsListResponseDto;
import com.web.clone.web.dto.PostsResponseDto;
import com.web.clone.web.dto.PostsSaveRequestDto;
import com.web.clone.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllByOrderByIdDesc().stream()
                .map(PostsListResponseDto::new)
                .toList();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    // 책에는 Transactional이 없는데 깃허브 코드에는 있음.
    @Transactional(readOnly = true)
    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new PostsResponseDto(entity);
    }

    public void delete(Long id) {
        postsRepository.deleteById(id);
    }
}
