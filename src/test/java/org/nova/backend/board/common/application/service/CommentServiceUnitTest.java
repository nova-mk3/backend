package org.nova.backend.board.common.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.nova.backend.annotation.FastTest;
import org.nova.backend.board.common.application.dto.request.CommentRequest;
import org.nova.backend.board.common.application.dto.request.UpdateCommentRequest;
import org.nova.backend.board.common.application.port.out.CommentPersistencePort;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.domain.exception.CommentDomainException;
import org.nova.backend.board.common.domain.model.entity.Comment;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.helper.MemberFixture;
import org.nova.backend.notification.application.port.in.NotificationUseCase;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentServiceUnitTest {

    private CommentService commentService;
    private CommentPersistencePort commentPersistencePort;
    private MemberRepository memberRepository;

    private Member writer;
    private Member other;

    @BeforeEach
    void setUp() {
        commentPersistencePort = mock(CommentPersistencePort.class);
        memberRepository = mock(MemberRepository.class);
        BasePostPersistencePort basePostPersistencePort = mock(BasePostPersistencePort.class);
        NotificationUseCase notificationUseCase = mock(NotificationUseCase.class);

        commentService = new CommentService(
                commentPersistencePort,
                basePostPersistencePort,
                memberRepository,
                mock(org.nova.backend.board.common.application.mapper.CommentMapper.class),
                notificationUseCase
        );

        ProfilePhoto profilePhoto = new ProfilePhoto(null, "test.jpg", "url");
        writer = MemberFixture.createStudent(profilePhoto);
        other = MemberFixture.createStudent(profilePhoto);
    }

    @FastTest
    @DisplayName("작성자가 아닌 사용자가 댓글 수정 시 예외가 발생해야 한다")
    void 댓글_수정_권한없음() {
        Comment comment = mock(Comment.class);
        when(comment.getMember()).thenReturn(writer);
        when(commentPersistencePort.findById(any())).thenReturn(Optional.of(comment));

        Throwable thrown = catchThrowable(() ->
                commentService.updateComment(UUID.randomUUID(), new UpdateCommentRequest("after"), other.getId()));
        
        assertThat(thrown).isInstanceOf(CommentDomainException.class)
                .hasMessageContaining("자신의 댓글만 수정");
    }

    @FastTest
    @DisplayName("작성자가 아니고 관리자도 아닌 사용자가 댓글 삭제 시 예외 발생")
    void 댓글_삭제_권한없음() {
        Comment comment = mock(Comment.class);
        when(comment.getMember()).thenReturn(writer);
        when(commentPersistencePort.findById(any())).thenReturn(Optional.of(comment));
        
        when(memberRepository.findById(other.getId())).thenReturn(Optional.of(other));

        Throwable thrown = catchThrowable(() -> commentService.deleteComment(UUID.randomUUID(), other.getId()));
        
        assertThat(thrown).isInstanceOf(CommentDomainException.class)
                .hasMessageContaining("삭제 권한이 없습니다");
    }

    @FastTest
    @DisplayName("댓글 내용이 빈 문자열이면 예외가 발생해야 한다")
    void 댓글_내용이_빈_문자열이면_예외() {
        CommentRequest request = new CommentRequest(null, "");
        Throwable thrown = catchThrowable(() -> commentService.addComment(UUID.randomUUID(), request, writer.getId()));
        assertThat(thrown).isInstanceOf(CommentDomainException.class)
                .hasMessageContaining("비어 있을 수 없습니다");
    }

    @FastTest
    @DisplayName("댓글 내용이 null이면 예외가 발생해야 한다")
    void 댓글_내용이_null이면_예외() {
        CommentRequest request = new CommentRequest(null, null);
        Throwable thrown = catchThrowable(() -> commentService.addComment(UUID.randomUUID(), request, writer.getId()));
        assertThat(thrown).isInstanceOf(CommentDomainException.class)
                .hasMessageContaining("비어 있을 수 없습니다");
    }
}