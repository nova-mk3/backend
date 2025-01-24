package org.nova.backend.board.application.service;

import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nova.backend.board.application.port.out.PostPersistencePort;
import org.nova.backend.board.domain.exception.BoardDomainException;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.board.domain.model.valueobject.Content;
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.nova.backend.board.domain.model.valueobject.Title;


class PostServiceTest {

    private PostService postService;
    private PostPersistencePort postPersistencePort;

    private Post samplePost;

    @BeforeEach
    void setUp() {
        postPersistencePort = mock(PostPersistencePort.class);
        postService = new PostService(postPersistencePort);

        samplePost = createSamplePost();
    }

    /**
     * 공통 테스트 데이터를 생성하는 메서드
     * @return Post 테스트 데이터
     */
    private Post createSamplePost() {
        return new Post(
                UUID.randomUUID(),
                null,
                PostType.FREE,
                new Title("첫 게시글이에요"),
                new Content("모두 잘부탁드려요"),
                0,
                0,
                0,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("새 게시글을 생성할 수 있다")
    void createPost() {
        // given
        when(postPersistencePort.save(any(Post.class))).thenReturn(samplePost);

        // when
        var savedPost = postService.createPost(samplePost);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getTitle().getTitle()).isEqualTo("첫 게시글이에요");

        verify(postPersistencePort, times(1)).save(samplePost);
    }

    @Test
    @DisplayName("모든 게시글을 정상적으로 조회할 수 있다")
    void getAllPosts() {
        // given
        when(postPersistencePort.findAllPosts()).thenReturn(Collections.singletonList(samplePost));

        // when
        List<Post> posts = postService.getAllPosts();

        // then
        assertThat(posts).hasSize(1);
        assertThat(posts.getFirst().getTitle().getTitle()).isEqualTo("첫 게시글이에요");

        verify(postPersistencePort, times(1)).findAllPosts();
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
    void findNonExistentPost() {
        // given
        UUID nonExistentPostId = UUID.randomUUID();
        when(postPersistencePort.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BoardDomainException.class, () -> postService.getPostById(nonExistentPostId));
    }
}
