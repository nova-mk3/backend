//package org.nova.backend.board.application.service;
//
//import java.util.ArrayList;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.nova.backend.board.application.port.out.PostPersistencePort;
//import org.nova.backend.board.domain.exception.BoardDomainException;
//import org.nova.backend.board.domain.exception.FileDomainException;
//import org.nova.backend.board.domain.model.entity.Post;
//import org.nova.backend.board.domain.model.valueobject.Content;
//import org.nova.backend.board.domain.model.valueobject.PostType;
//import org.nova.backend.board.domain.model.valueobject.Title;
//import org.springframework.web.multipart.MultipartFile;
//
//
//class FreePostServiceTest {
//
//    private FreePostService freePostService;
//    private PostPersistencePort postPersistencePort;
//    private FileService fileService;
//
//    private Post samplePost;
//
//    @BeforeEach
//    void setUp() {
//        postPersistencePort = mock(PostPersistencePort.class);
//        fileService = mock(FileService.class);
//        freePostService = new FreePostService(postPersistencePort, fileService);
//
//        samplePost = createSamplePost();
//    }
//
//    /**
//     * 공통 테스트 데이터를 생성하는 메서드
//     * @return Post 테스트 데이터
//     */
//    private Post createSamplePost() {
//        return new Post(
//                UUID.randomUUID(),
//                null,
//                PostType.FREE,
//                "첫 게시글이에요",
//                "모두 잘부탁드려요",
//                0,
//                0,
//                0,
//                new ArrayList<>(),
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//    }
//
//    @Test
//    @DisplayName("모든 게시글을 정상적으로 조회할 수 있다")
//    void getAllPosts() {
//        // given
//        when(postPersistencePort.findAllPosts()).thenReturn(Collections.singletonList(samplePost));
//
//        // when
//        List<Post> posts = freePostService.getAllPosts();
//
//        // then
//        assertThat(posts).hasSize(1);
//        assertThat(posts.getFirst().getTitle()).isEqualTo("첫 게시글이에요");
//
//        verify(postPersistencePort, times(1)).findAllPosts();
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
//    void findNonExistentPost() {
//        // given
//        UUID nonExistentPostId = UUID.randomUUID();
//        when(postPersistencePort.findById(nonExistentPostId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(BoardDomainException.class, () -> freePostService.getPostById(nonExistentPostId));
//    }
//
//    @Test
//    @DisplayName("새 게시글을 생성할 수 있다")
//    void createPost() {
//        // given
//        List<MultipartFile> mockFiles = List.of(mock(MultipartFile.class), mock(MultipartFile.class));
//        when(postPersistencePort.save(any(Post.class))).thenReturn(samplePost);
//
//        // when
//        Post savedPost = freePostService.createPost(samplePost, mockFiles);
//
//        // then
//        assertThat(savedPost).isNotNull();
//        assertThat(savedPost.getTitle()).isEqualTo("첫 게시글이에요");
//        verify(postPersistencePort, times(1)).save(samplePost);
//        verify(fileService, times(1)).saveFiles(samplePost, mockFiles);
//    }
//
//    @Test
//    @DisplayName("파일 저장 중 예외 발생 시 트랜잭션 롤백")
//    void createPostFailure() {
//        // given
//        List<MultipartFile> mockFiles = List.of(mock(MultipartFile.class));
//        when(postPersistencePort.save(any(Post.class))).thenReturn(samplePost);
//        doThrow(new FileDomainException("파일 저장 중 오류 발생")).when(fileService).saveFiles(samplePost, mockFiles);
//
//        // when & then
//        assertThrows(FileDomainException.class, () -> freePostService.createPost(samplePost, mockFiles));
//
//        // then
//        verify(postPersistencePort, times(1)).save(samplePost);
//        verify(fileService, times(1)).saveFiles(samplePost, mockFiles);
//    }
//
//    @Test
//    @DisplayName("제목 길이가 255자를 초과할 경우 예외 발생")
//    void createPostWithLongTitle() {
//        // given
//        String longTitle = "a".repeat(256);
//
//        // when
//        BoardDomainException exception = assertThrows(BoardDomainException.class, () -> new Title(longTitle));
//
//        // then
//        assertThat(exception.getMessage()).isEqualTo("제목은 255자를 초과할 수 없습니다.");
//    }
//
//    @Test
//    @DisplayName("내용 길이가 5000자를 초과할 경우 예외 발생")
//    void createPostWithLongContent() {
//        // given
//        String longContent = "a".repeat(5001);
//
//        // when
//        BoardDomainException exception = assertThrows(BoardDomainException.class, () -> new Content(longContent));
//
//        // then
//        assertThat(exception.getMessage()).isEqualTo("내용은 5000자를 초과할 수 없습니다.");
//    }
//}
