package org.nova.backend.board.common.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.nova.backend.annotation.FastTest;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.application.port.out.CommentPersistencePort;
import org.nova.backend.board.common.application.port.out.PostLikePersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.entity.PostLike;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.member.helper.MemberFixture;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.shared.security.BoardSecurityChecker;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasePostServiceUnitTest {

    private BasePostService basePostService;
    private MemberRepository memberRepository;
    private BasePostPersistencePort basePostPersistencePort;
    private BoardUseCase boardUseCase;
    private PostLikePersistencePort postLikePersistencePort;

    private Member normalUser;
    private Member adminUser;
    private Board integratedBoard;

    private BoardSecurityChecker boardSecurityChecker;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        basePostPersistencePort = mock(BasePostPersistencePort.class);
        boardUseCase = mock(BoardUseCase.class);
        postLikePersistencePort = mock(PostLikePersistencePort.class);
        SecurityUtil securityUtil = mock(SecurityUtil.class);
        NotificationUseCase notificationUseCase = mock(NotificationUseCase.class);
        CommentPersistencePort commentPersistencePort = mock(CommentPersistencePort.class);
        boardSecurityChecker = mock(BoardSecurityChecker.class);

        basePostService = new BasePostService(
                basePostPersistencePort,
                commentPersistencePort,
                postLikePersistencePort,
                memberRepository,
                boardSecurityChecker,
                boardUseCase,
                mock(org.nova.backend.board.common.application.port.in.FileUseCase.class),
                securityUtil,
                mock(org.nova.backend.board.common.application.mapper.BasePostMapper.class),
                mock(org.nova.backend.board.clubArchive.application.mapper.PicturePostMapper.class),
                mock(org.nova.backend.board.common.application.mapper.AllPostMapper.class),
                notificationUseCase
        );

        ProfilePhoto profilePhoto = new ProfilePhoto(null, "test.jpg", "url");
        normalUser = MemberFixture.createStudent(profilePhoto);
        adminUser = MemberFixture.createStudent(profilePhoto);
        adminUser.updateRole(Role.ADMINISTRATOR);

        integratedBoard = new Board(UUID.randomUUID(), BoardCategory.INTEGRATED);
    }

    @FastTest
    @DisplayName("게시글 작성 시 제목이 비어있으면 예외가 발생해야 한다")
    void 제목이_비어있으면_예외발생() {
        BasePostRequest request = new BasePostRequest(" ", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("제목은 비어 있을 수 없습니다");
    }

    @FastTest
    @DisplayName("게시글 작성 시 내용이 NULL이면 예외가 발생해야 한다")
    void 내용이_NULL이면_예외발생() {
        BasePostRequest request = new BasePostRequest("제목", null, PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("내용은 비어 있을 수 없습니다.");
    }

    @FastTest
    @DisplayName("일반 유저가 NOTICE 타입 게시글 작성 시 예외가 발생해야 한다")
    void 일반유저가_NOTICE_작성시_예외발생() {
        BasePostRequest request = new BasePostRequest("공지", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        when(memberRepository.findById(normalUser.getId())).thenReturn(Optional.of(normalUser));
        when(boardSecurityChecker.isAdminOrPresident(any())).thenReturn(false);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, normalUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("공지사항은 관리자 또는 회장만 작성");
    }

    @FastTest
    @DisplayName("잘못된 게시판 타입으로 게시글 작성 시 예외가 발생해야 한다")
    void 잘못된_게시판_타입이면_예외() {
        Board clubBoard = new Board(UUID.randomUUID(), BoardCategory.CLUB_ARCHIVE);
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.NOTICE, null);
        when(boardUseCase.getBoardById(clubBoard.getId())).thenReturn(clubBoard);
        when(memberRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(boardSecurityChecker.isAdminOrPresident(any())).thenReturn(true);

        Throwable thrown = catchThrowable(() -> basePostService.createPost(clubBoard.getId(), request, adminUser.getId()));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("타입의 게시글을 저장할 수 없습니다");
    }

    @FastTest
    @DisplayName("존재하지 않는 사용자로 게시글 작성 시 예외가 발생해야 한다")
    void 존재하지_않는_사용자면_예외() {
        UUID fakeUserId = UUID.randomUUID();
        BasePostRequest request = new BasePostRequest("제목", "내용", PostType.FREE, null);
        when(boardUseCase.getBoardById(integratedBoard.getId())).thenReturn(integratedBoard);
        when(memberRepository.findById(fakeUserId)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> basePostService.createPost(integratedBoard.getId(), request, fakeUserId));
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @FastTest
    @DisplayName("게시글 삭제 시 작성자가 아니고 관리자도 아닌 경우 예외가 발생해야 한다")
    void 게시글_삭제_작성자가_아니고_관리자도_아니면_예외() {
        Post post = mock(Post.class);
        when(post.getMember()).thenReturn(normalUser);
        when(post.getBoard()).thenReturn(integratedBoard);
        when(basePostPersistencePort.findById(any())).thenReturn(Optional.of(post));
        
        Member stranger = mock(Member.class);
        when(stranger.getId()).thenReturn(UUID.randomUUID());
        when(stranger.getRole()).thenReturn(Role.GENERAL);
        when(memberRepository.findById(any())).thenReturn(Optional.of(stranger));

        Throwable thrown = catchThrowable(() -> basePostService.deletePost(integratedBoard.getId(), UUID.randomUUID(), stranger.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("권한이 없습니다");
    }

    @FastTest
    @DisplayName("이미 좋아요한 사용자가 다시 좋아요 시도 시 예외가 발생해야 한다")
    void 이미_좋아요한_사용자가_또_좋아요시_예외() {
        when(memberRepository.findById(any())).thenReturn(Optional.of(normalUser));
        when(basePostPersistencePort.findById(any())).thenReturn(Optional.of(mock(Post.class)));
        when(postLikePersistencePort.findByPostIdAndMemberId(any(), any())).thenReturn(Optional.of(mock(PostLike.class)));

        Throwable thrown = catchThrowable(() -> basePostService.likePost(UUID.randomUUID(), normalUser.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("이미 좋아요를 눌렀습니다.");
    }

    @FastTest
    @DisplayName("좋아요하지 않은 상태에서 취소 시도 시 예외가 발생해야 한다")
    void 좋아요_하지_않은_상태에서_취소시_예외() {
        when(basePostPersistencePort.findById(any())).thenReturn(Optional.of(mock(Post.class)));
        when(postLikePersistencePort.findByPostIdAndMemberId(any(), any())).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> basePostService.unlikePost(UUID.randomUUID(), normalUser.getId()));
        assertThat(thrown).isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("좋아요를 누르지 않은 게시글입니다.");
    }
}