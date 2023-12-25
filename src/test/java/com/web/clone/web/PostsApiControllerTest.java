package com.web.clone.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.clone.domain.posts.Posts;
import com.web.clone.domain.posts.PostsRepository;
import com.web.clone.web.dto.PostsSaveRequestDto;
import com.web.clone.web.dto.PostsUpdateRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

// 통합 테스트?

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PostsApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void teardown() {
        postsRepository.deleteAll();
    }

    private String getUrl(String url) {
        return "http://localhost:" + port + url;
    }

    @Test
    public void Posts_등록된다() {
        // given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = getUrl("/api/v1/posts");

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertAll(
                () -> Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> Assertions.assertThat(responseEntity.getBody()).isGreaterThan(0L)
        );

        List<Posts> all = postsRepository.findAll();
        Posts posts = all.get(0);

        assertAll(
                () -> Assertions.assertThat(posts.getTitle()).isEqualTo(title),
                () -> Assertions.assertThat(posts.getContent()).isEqualTo(content)
        );
    }

    @Test
    public void Posts_수정() {
        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = getUrl("/api/v1/posts/" + updateId);

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        // then
        assertAll(
                () -> Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> Assertions.assertThat(responseEntity.getBody()).isGreaterThan(0L)
        );

        List<Posts> all = postsRepository.findAll();
        Posts posts = all.get(0);

        assertAll(
                () -> Assertions.assertThat(posts.getTitle()).isEqualTo(expectedTitle),
                () -> Assertions.assertThat(posts.getContent()).isEqualTo(expectedContent)
        );
    }

    @Test
    public void Posts_수정_mockMvc() throws Exception {
        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = getUrl("/api/v1/posts/" + updateId);

        System.out.println(url);
        System.out.println(new ObjectMapper().writeValueAsString(requestDto));

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // then
        List<Posts> all = postsRepository.findAll();
        Posts posts = all.get(0);

        assertAll(
                () -> Assertions.assertThat(posts.getTitle()).isEqualTo(expectedTitle),
                () -> Assertions.assertThat(posts.getContent()).isEqualTo(expectedContent)
        );
    }
}