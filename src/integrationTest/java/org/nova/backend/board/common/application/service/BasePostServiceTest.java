package org.nova.backend.board.common.application.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nova.backend.board.common.adapter.persistence.repository.BoardRepository;
import org.nova.backend.board.common.adapter.persistence.repository.CommentRepository;
import org.nova.backend.board.common.adapter.persistence.repository.FileRepository;
import org.nova.backend.board.common.adapter.persistence.repository.PostRepository;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdateBasePostRequest;
import org.nova.backend.board.common.application.dto.response.BasePostDetailResponse;
import org.nova.backend.board.common.application.dto.response.BasePostSummaryResponse;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.Comment;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.member.helper.MemberFixture;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.support.AbstractIntegrationTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
class BasePostServiceTest extends AbstractIntegrationTest {

    private static final String HELLO_TITLE = "안녕하세요";

    private final BasePostService basePostService;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;
    private final EntityManager entityManager;

    @MockitoBean BoardUseCase boardUseCase;
    @MockitoBean FileUseCase fileUseCase;
    @MockitoBean SecurityUtil securityUtil;
    @MockitoBean NotificationUseCase notificationUseCase;

    private Member normalUser;
    private Member normalUser2;
    private Member adminUser;
    private Board integratedBoard;

    @BeforeEach
    void setUp() {
        ProfilePhoto profilePhoto = new ProfilePhoto(null, "test.jpg", "https://example.com/test.jpg");
        normalUser = MemberFixture.createStudent(profilePhoto);
        normalUser2 = MemberFixture.createStudent(profilePhoto);
        adminUser = MemberFixture.createStudent(profilePhoto);
        adminUser.updateRole(Role.ADMINISTRATOR);
        memberRepository.saveAll(List.of(normalUser, normalUser2, adminUser));

        integratedBoard = boardRepository.save(new Board(UUID.randomUUID(), BoardCategory.INTEGRATED));

        when(boardUseCase.getBoardById(any(UUID.class))).thenReturn(integratedBoard);

        doAnswer(invocation -> {
            List<UUID> fileIds = invocation.getArgument(0);
            fileRepository.deleteAllById(fileIds);
            return null;
        }).when(fileUseCase).deleteFiles(any());
    }

    @Test
    @DisplayName("파일이 포함된 게시글 작성이 성공적으로 이루어져야 한다")
    @Transactional
    void 파일이_포함된_게시글_작성_성공() {
        File file1 = fileRepository.save(new File(null, "file1", "/path/file1", null, 0));
        File file2 = fileRepository.save(new File(null, "file2", "/path/file2", null, 0));
        List<UUID> fileIds = List.of(file1.getId(), file2.getId());

        BasePostRequest request = new BasePostRequest("파일포함", "파일 내용", PostType.FREE, fileIds);

        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        when(fileUseCase.findFilesByIds(fileIds)).thenReturn(List.of(file1, file2));

        BasePostDetailResponse response = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        assertThat(response.getTitle()).isEqualTo("파일포함");
        assertThat(response.getContent()).contains("파일 내용");
        assertThat(file1.getPost()).isNotNull();
        assertThat(file2.getPost()).isNotNull();
    }

    @Test
    @DisplayName("동일한 유저가 같은 제목으로 여러 번 게시글을 작성할 수 있어야 한다")
    @Transactional
    void 동일한_유저가_같은_제목으로_여러번_게시글_작성_가능() {
        BasePostRequest request = new BasePostRequest("중복제목", "첫번째 내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        BasePostDetailResponse r1 = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());
        BasePostDetailResponse r2 = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        assertThat(r1.getTitle()).isEqualTo("중복제목");
        assertThat(r2.getTitle()).isEqualTo("중복제목");
        assertThat(r1.getId()).isNotEqualTo(r2.getId());
    }

    @Test
    @DisplayName("일반 유저가 FREE 타입 게시글 작성이 성공적으로 이루어져야 한다")
    @Transactional
    void 일반유저가_FREE_게시글_작성_성공() {
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        var response = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("관리자 사용자가 NOTICE 타입 게시글 작성이 성공적으로 이루어져야 한다")
    void 관리자_사용자가_NOTICE_작성_성공() {
        BasePostRequest request = new BasePostRequest("관리자공지", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        var response = basePostService.createPost(integratedBoard.getId(), request, adminUser.getId());

        assertThat(response.getTitle()).isEqualTo("관리자공지");
    }

    @Test
    @DisplayName("존재하지 않는 파일 ID로 게시글 작성 시 예외가 발생해야 한다")
    @Transactional
    void 존재하지_않는_파일ID_포함시_예외발생() {
        UUID fakeFileId = UUID.randomUUID();
        List<UUID> fileIds = List.of(fakeFileId);
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, fileIds);

        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        when(fileUseCase.findFilesByIds(fileIds)).thenThrow(new BoardDomainException("파일을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("파일을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("게시글 삭제 시 관리자는 본인 글이 아니어도 삭제가 가능해야 한다")
    @Transactional
    void 게시글_삭제_관리자는_본인글_아니어도_삭제_가능() {
        BasePostRequest request = new BasePostRequest("삭제 테스트", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        basePostService.deletePost(integratedBoard.getId(), post.getId(), adminUser.getId());

        Throwable thrown = catchThrowable(() -> basePostService.getPostById(integratedBoard.getId(), post.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("게시글 수정 시 파일 삭제가 정상적으로 이루어져야 한다")
    @Transactional
    void 게시글_수정_파일_삭제_정상작동() {
        File file1 = fileRepository.save(new File(null, "file1", "/path/file1", null, 0));
        List<UUID> fileIds = List.of(file1.getId());

        BasePostRequest create = new BasePostRequest("수정전", "내용", PostType.FREE, fileIds);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        when(fileUseCase.findFilesByIds(fileIds)).thenReturn(List.of(file1));

        var post = basePostService.createPost(integratedBoard.getId(), create, normalUser.getId());

        UpdateBasePostRequest update = new UpdateBasePostRequest(PostType.FREE, "수정후", "수정내용", null, fileIds);
        basePostService.updatePost(integratedBoard.getId(), post.getId(), update, normalUser.getId());

        assertThat(fileRepository.findById(file1.getId())).isNotPresent();
    }

    @Test
    @DisplayName("게시글에 좋아요가 성공적으로 추가되어야 한다")
    @Transactional
    void 게시글에_좋아요_성공() {
        BasePostRequest request = new BasePostRequest(HELLO_TITLE, "내용", PostType.INTRODUCTION, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());
        int count = basePostService.likePost(post.getId(), normalUser2.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 취소가 성공적으로 이루어져야 한다")
    @Transactional
    void 좋아요_취소_성공() {
        BasePostRequest request = new BasePostRequest(HELLO_TITLE, "내용", PostType.QNA, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());
        basePostService.likePost(post.getId(), normalUser2.getId());

        int count = basePostService.unlikePost(post.getId(), normalUser2.getId());
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("게시글 조회 시 조회수가 정상적으로 증가해야 한다")
    @Transactional
    void 게시글_조회시_조회수_증가() {
        BasePostRequest request = new BasePostRequest(HELLO_TITLE, "내용", PostType.INTRODUCTION, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        basePostService.getPostById(integratedBoard.getId(), post.getId());
        basePostService.getPostById(integratedBoard.getId(), post.getId());

        entityManager.flush();
        entityManager.clear();

        Post updated = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        assertThat(updated.getViewCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시판 타입별 최신글 조회가 정상적으로 이루어져야 한다")
    @Transactional
    void 게시판_타입별_최신글_조회() {
        Map<PostType, List<BasePostSummaryResponse>> result = basePostService.getLatestPostsByType(integratedBoard.getId());
        assertThat(result).containsKeys(PostType.QNA, PostType.FREE, PostType.NOTICE, PostType.INTRODUCTION);
    }

    @Test
    @DisplayName("댓글이 있는 게시글 삭제 시 FK 제약 위반 없이 댓글과 게시글이 모두 삭제되어야 한다")
    @Transactional
    void 댓글이_있는_게시글_삭제_FK_위반_없음() {
        BasePostRequest request = new BasePostRequest("댓글포함 게시글", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        var postResponse = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        Post savedPost = postRepository.findById(postResponse.getId()).orElseThrow();
        Member managedUser = memberRepository.findById(normalUser.getId()).orElseThrow();
        Comment comment = new Comment(null, savedPost, managedUser, null, "테스트 댓글", LocalDateTime.now(), null);
        Comment saved = commentRepository.save(comment);

        assertThatCode(() -> basePostService.deletePost(integratedBoard.getId(), postResponse.getId(), normalUser.getId()))
                .doesNotThrowAnyException();

        entityManager.flush();
        entityManager.clear();

        assertThat(postRepository.findById(postResponse.getId())).isNotPresent();
        assertThat(commentRepository.findById(saved.getId())).isNotPresent();
    }
}