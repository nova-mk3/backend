package org.nova.backend.board.common.application.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.nova.backend.board.common.adapter.persistence.repository.FileRepository;
import org.nova.backend.board.common.adapter.persistence.repository.PostRepository;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdateBasePostRequest;
import org.nova.backend.board.common.application.dto.response.BasePostDetailResponse;
import org.nova.backend.board.common.application.dto.response.BasePostSummaryResponse;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.member.helper.MemberFixture;
import org.nova.backend.board.common.adapter.persistence.repository.BoardRepository;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.support.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(BasePostServiceTest.MockConfig.class)
class BasePostServiceTest extends AbstractIntegrationTest {

    @Autowired BasePostService basePostService;
    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired FileRepository fileRepository;
    @Autowired PostRepository postRepository;
    @Autowired BoardUseCase boardUseCase;
    @Autowired FileUseCase fileUseCase;
    @Autowired EntityManager entityManager;

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
    @DisplayName("게시글 작성 시 제목이 비어있으면 예외가 발생해야 한다")
    @Transactional
    void 제목이_비어있으면_예외발생() {
        BasePostRequest request = new BasePostRequest(" ", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("제목은 비어 있을 수 없습니다");
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
    @DisplayName("게시글 작성 시 내용이 NULL이면 예외가 발생해야 한다")
    @Transactional
    void 내용이_NULL이면_예외발생() {
        BasePostRequest request = new BasePostRequest("제목", null, PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("내용은 비어 있을 수 없습니다.");
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
    @DisplayName("일반 유저가 NOTICE 타입 게시글 작성 시 예외가 발생해야 한다")
    void 일반유저가_NOTICE_작성시_예외발생() {
        BasePostRequest request = new BasePostRequest("공지", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("공지사항은 관리자 또는 회장만 작성");
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
    @DisplayName("잘못된 게시판 타입으로 게시글 작성 시 예외가 발생해야 한다")
    void 잘못된_게시판_타입이면_예외() {
        Board clubBoard = boardRepository.save(new Board(UUID.randomUUID(), BoardCategory.CLUB_ARCHIVE));
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(clubBoard.getId())).thenReturn(clubBoard);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(clubBoard.getId(), request, adminUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("타입의 게시글을 저장할 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 게시글 작성 시 예외가 발생해야 한다")
    void 존재하지_않는_사용자면_예외() {
        UUID fakeUserId = UUID.randomUUID();
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, fakeUserId));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 파일 ID로 게시글 작성 시 예외가 발생해야 한다")
    @Transactional
    void 존재하지_않는_파일ID_포함시_예외발생() {
        UUID fakeFileId = UUID.randomUUID();
        List<UUID> fileIds = List.of(fakeFileId);
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, fileIds);

        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        when(fileUseCase.findFilesByIds(fileIds)).thenThrow(new BoardDomainException("파일을 찾을 수 없습니다"));

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("파일을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("게시글 삭제 시 작성자가 아니고 관리자도 아닌 경우 예외가 발생해야 한다")
    @Transactional
    void 게시글_삭제_작성자가_아니고_관리자도_아니면_예외() {
        BasePostRequest request = new BasePostRequest("삭제 테스트", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        Member stranger = MemberFixture.createStudent(new ProfilePhoto(null, "s2", "url"));
        memberRepository.save(stranger);

        Throwable thrown = catchThrowable(() -> basePostService.deletePost(integratedBoard.getId(), post.getId(), stranger.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("권한이 없습니다");
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
        BasePostRequest request = new BasePostRequest("안녕하세요", "내용", PostType.INTRODUCTION, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());
        int count = basePostService.likePost(post.getId(), normalUser2.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 좋아요한 사용자가 다시 좋아요 시도 시 예외가 발생해야 한다")
    @Transactional
    void 이미_좋아요한_사용자가_또_좋아요시_예외() {
        BasePostRequest request = new BasePostRequest("안녕하세요", "내용", PostType.INTRODUCTION, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());
        basePostService.likePost(post.getId(), normalUser2.getId());

        Throwable thrown = catchThrowable(() -> basePostService.likePost(post.getId(), normalUser2.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("이미 좋아요를 눌렀습니다.");
    }

    @Test
    @DisplayName("좋아요 취소가 성공적으로 이루어져야 한다")
    @Transactional
    void 좋아요_취소_성공() {
        BasePostRequest request = new BasePostRequest("안녕하세요", "내용", PostType.QNA, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());
        basePostService.likePost(post.getId(), normalUser2.getId());

        int count = basePostService.unlikePost(post.getId(), normalUser2.getId());
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("좋아요하지 않은 상태에서 취소 시도 시 예외가 발생해야 한다")
    @Transactional
    void 좋아요_하지_않은_상태에서_취소시_예외() {
        BasePostRequest request = new BasePostRequest("안녕하세요", "내용", PostType.INTRODUCTION, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        Throwable thrown = catchThrowable(() -> basePostService.unlikePost(post.getId(), normalUser2.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("좋아요를 누르지 않은 게시글입니다.");
    }

    @Test
    @DisplayName("게시글 조회 시 조회수가 정상적으로 증가해야 한다")
    @Transactional
    void 게시글_조회시_조회수_증가() {
        BasePostRequest request = new BasePostRequest("안녕하세요", "내용", PostType.INTRODUCTION, null);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        basePostService.getPostById(integratedBoard.getId(), post.getId());
        basePostService.getPostById(integratedBoard.getId(), post.getId());

        entityManager.flush();
        entityManager.clear();

        Post updated = postRepository.findById(post.getId()).get();
        assertThat(updated.getViewCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시판 타입별 최신글 조회가 정상적으로 이루어져야 한다")
    @Transactional
    void 게시판_타입별_최신글_조회() {
        Map<PostType, List<BasePostSummaryResponse>> result = basePostService.getLatestPostsByType(integratedBoard.getId());
        assertThat(result).containsKeys(PostType.QNA, PostType.FREE, PostType.NOTICE, PostType.INTRODUCTION);
    }

    @TestConfiguration
    static class MockConfig {
        @Bean public SecurityUtil securityUtil() { return mock(SecurityUtil.class); }
        @Bean public NotificationUseCase notificationUseCase() { return mock(NotificationUseCase.class); }
        @Bean public FileUseCase fileUseCase() { return mock(FileUseCase.class); }
        @Bean public BoardUseCase boardUseCase() { return mock(BoardUseCase.class); }
    }
}