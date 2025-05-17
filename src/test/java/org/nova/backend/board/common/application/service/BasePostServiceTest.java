package org.nova.backend.board.common.application.service;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nova.backend.board.common.adapter.persistence.repository.FileRepository;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdateBasePostRequest;
import org.nova.backend.board.common.application.dto.response.BasePostDetailResponse;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.File;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(BasePostServiceTest.MockConfig.class)
class BasePostServiceTest {

    @Autowired BasePostService basePostService;
    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired FileRepository fileRepository;
    @Autowired BoardUseCase boardUseCase;
    @Autowired FileUseCase fileUseCase;

    private Member normalUser;
    private Member adminUser;
    private Board integratedBoard;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeEach
    void setUp() {
        ProfilePhoto profilePhoto = new ProfilePhoto(null, "test.jpg", "https://example.com/test.jpg");
        normalUser = MemberFixture.createStudent(profilePhoto);
        adminUser = MemberFixture.createStudent(profilePhoto);
        adminUser.updateRole(Role.ADMINISTRATOR);
        memberRepository.saveAll(List.of(normalUser, adminUser));

        integratedBoard = boardRepository.save(new Board(UUID.randomUUID(), BoardCategory.INTEGRATED));

        doAnswer(invocation -> {
            List<UUID> fileIds = invocation.getArgument(0);
            fileRepository.deleteAllById(fileIds);
            return null;
        }).when(fileUseCase).deleteFiles(any());
    }

    @Test
    @Transactional
    void 제목이_NULL이면_예외발생() {
        BasePostRequest request = new BasePostRequest("", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        assertThatThrownBy(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("제목은 비어 있을 수 없습니다");
    }

    @Test
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
    @Transactional
    void 내용이_NULL이면_예외발생() {
        BasePostRequest request = new BasePostRequest("제목", null, PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        assertThatThrownBy(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("내용은 비어 있을 수 없습니다.");
    }

    @Test
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
    @Transactional
    void 일반유저가_FREE_게시글_작성_성공() {
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        var response = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContent()).isEqualTo("내용");
    }

    @Test
    void 일반유저가_NOTICE_작성시_예외발생() {
        BasePostRequest request = new BasePostRequest("공지", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        assertThatThrownBy(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("공지사항은 관리자 또는 회장만 작성");
    }

    @Test
    void 관리자_사용자가_NOTICE_작성_성공() {
        BasePostRequest request = new BasePostRequest("관리자공지", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        var response = basePostService.createPost(integratedBoard.getId(), request, adminUser.getId());

        assertThat(response.getTitle()).isEqualTo("관리자공지");
    }

    @Test
    void 잘못된_게시판_타입이면_예외() {
        Board clubBoard = boardRepository.save(new Board(UUID.randomUUID(), BoardCategory.CLUB_ARCHIVE));
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(clubBoard.getId())).thenReturn(clubBoard);

        assertThatThrownBy(() -> basePostService.createPost(clubBoard.getId(), request, adminUser.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("타입의 게시글을 저장할 수 없습니다");
    }

    @Test
    void 존재하지_않는_사용자면_예외() {
        UUID fakeUserId = UUID.randomUUID();
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        assertThatThrownBy(() -> basePostService.createPost(integratedBoard.getId(), request, fakeUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @Transactional
    void 존재하지_않는_파일ID_포함시_예외발생() {
        UUID fakeFileId = UUID.randomUUID();
        List<UUID> fileIds = List.of(fakeFileId);
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, fileIds);

        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        when(fileUseCase.findFilesByIds(fileIds)).thenThrow(new BoardDomainException("파일을 찾을 수 없습니다"));

        assertThatThrownBy(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()))
                .isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("파일을 찾을 수 없습니다");
    }

    @Test
    @Transactional
    void 게시글_삭제_작성자가_아니고_관리자도_아니면_예외() {
        BasePostRequest request = new BasePostRequest("삭제 테스트", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        Member stranger = MemberFixture.createStudent(new ProfilePhoto(null, "s2", "url"));
        memberRepository.save(stranger);

        assertThatThrownBy(() -> basePostService.deletePost(integratedBoard.getId(), post.getId(), stranger.getId()))
                .isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("권한이 없습니다");
    }

    @Test
    @Transactional
    void 게시글_삭제_관리자는_본인글_아니어도_삭제_가능() {
        BasePostRequest request = new BasePostRequest("삭제 테스트", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        var post = basePostService.createPost(integratedBoard.getId(), request, normalUser.getId());

        basePostService.deletePost(integratedBoard.getId(), post.getId(), adminUser.getId());

        assertThatThrownBy(() -> basePostService.getPostById(integratedBoard.getId(), post.getId()))
                .isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다.");
    }

    @Test
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

    @TestConfiguration
    static class MockConfig {
        @Bean public SecurityUtil securityUtil() { return mock(SecurityUtil.class); }
        @Bean public NotificationUseCase notificationUseCase() { return mock(NotificationUseCase.class); }
        @Bean public FileUseCase fileUseCase() { return mock(FileUseCase.class); }
        @Bean public BoardUseCase boardUseCase() { return mock(BoardUseCase.class); }
    }
}