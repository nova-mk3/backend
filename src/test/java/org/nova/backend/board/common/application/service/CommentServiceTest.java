package org.nova.backend.board.common.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nova.backend.board.common.adapter.persistence.repository.BoardRepository;
import org.nova.backend.board.common.adapter.persistence.repository.CommentRepository;
import org.nova.backend.board.common.adapter.persistence.repository.PostRepository;
import org.nova.backend.board.common.application.dto.request.CommentRequest;
import org.nova.backend.board.common.application.dto.request.UpdateCommentRequest;
import org.nova.backend.board.common.application.dto.response.CommentResponse;
import org.nova.backend.board.common.domain.exception.CommentDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.member.helper.MemberFixture;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.notification.domain.model.entity.valueobject.EventType;
import org.nova.backend.support.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(CommentServiceTest.MockConfig.class)
class CommentServiceTest extends AbstractIntegrationTest {

    @Autowired BoardRepository boardRepository;
    @Autowired CommentService commentService;
    @Autowired CommentRepository commentRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired NotificationUseCase notificationUseCase;
    @Autowired EntityManager entityManager;

    private Member writer;
    private Member other;
    private Member admin;
    private Board integratedBoard;
    private Post post;

    @BeforeEach
    void setUp() {
        ProfilePhoto profilePhoto = new ProfilePhoto(null, "test.jpg", "https://example.com/test.jpg");
        writer = MemberFixture.createStudent(profilePhoto);
        other = MemberFixture.createStudent(profilePhoto);
        admin = MemberFixture.createStudent(profilePhoto);
        admin.updateRole(Role.ADMINISTRATOR);
        memberRepository.saveAll(List.of(writer, other, admin));

        integratedBoard = boardRepository.save(new Board(UUID.randomUUID(), BoardCategory.INTEGRATED));
        post = postRepository.save(new Post(
                UUID.randomUUID(),
                writer,
                integratedBoard,
                PostType.FREE,
                "제목",
                "내용",
                0,
                0,
                0,
                0,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
    }

    @Test
    @DisplayName("댓글 작성이 성공적으로 이루어져야 한다")
    @Transactional
    void 댓글_작성_성공() {
        CommentRequest request = new CommentRequest(null, "첫 댓글");

        CommentResponse response = commentService.addComment(post.getId(), request, writer.getId());

        assertThat(response.getContent()).isEqualTo("첫 댓글");
        Post updated = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updated.getCommentCount()).isEqualTo(1);
        verify(notificationUseCase, never()).create(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("대댓글 작성 시 상위 댓글 작성자에게 알림이 발송되어야 한다")
    @Transactional
    void 대댓글_작성시_알림_발송() {
        CommentRequest parentReq = new CommentRequest(null, "부모");
        CommentResponse parentRes =
                commentService.addComment(post.getId(), parentReq, writer.getId());
        UUID parentId = parentRes.getId();

        CommentRequest childReq = new CommentRequest(parentId, "대댓글");
        commentService.addComment(post.getId(), childReq, other.getId());

        verify(notificationUseCase).create(
                writer.getId(),
                EventType.REPLY,
                post.getId(),
                post.getPostType(),
                other.getName()
        );

        assertThat(postRepository.findById(post.getId()).orElseThrow().getCommentCount())
                .isEqualTo(2);
    }

    @Test
    @DisplayName("댓글 수정은 작성자 본인만 가능해야 한다")
    @Transactional
    void 댓글_수정_본인만_가능() {
        UUID commentId = commentService.addComment(post.getId(), new CommentRequest(null, "before"), writer.getId())
                .getId();
        UpdateCommentRequest update = new UpdateCommentRequest("after");

        CommentResponse res = commentService.updateComment(commentId, update, writer.getId());

        assertThat(res.getContent()).isEqualTo("after");
        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(commentId).orElseThrow().getContent()).isEqualTo("after");
    }

    @Test
    @DisplayName("작성자가 아닌 사용자가 댓글 수정 시 예외가 발생해야 한다")
    @Transactional
    void 댓글_수정_권한없음() {
        UUID commentId = commentService.addComment(post.getId(), new CommentRequest(null, "before"), writer.getId())
                .getId();
        Throwable thrown = catchThrowable(() ->
                commentService.updateComment(commentId, new UpdateCommentRequest("after"), other.getId()));
        assertThat(thrown).isInstanceOf(CommentDomainException.class)
                .hasMessageContaining("자신의 댓글만 수정");
    }

//    @Test
//    @DisplayName("댓글 삭제 시 자식 댓글까지 함께 삭제되어야 한다")
//    @Transactional
//    void 댓글_삭제_자식까지() {
//        UUID parentId = commentService.addComment(post.getId(), new CommentRequest(null, "부모"), writer.getId())
//                .getId();
//        commentService.addComment(post.getId(), new CommentRequest(parentId, "자식"), other.getId());
//        assertThat(post.getCommentCount()).isEqualTo(2);
//
//        commentService.deleteComment(parentId, writer.getId());
//
//        entityManager.flush();
//        entityManager.clear();
//
//        assertThat(commentRepository.existsById(parentId)).isFalse();
//        assertThat(commentRepository.findAllByParentCommentId(parentId)).isEmpty();
//
//        assertThat(postRepository.findById(post.getId()).orElseThrow()
//                .getCommentCount())
//                .isZero();
//    }

    @Test
    @DisplayName("작성자가 아니고 관리자도 아닌 사용자가 댓글 삭제 시 예외 발생")
    @Transactional
    void 댓글_삭제_권한없음() {
        UUID commentId = commentService.addComment(post.getId(), new CommentRequest(null, "부모"), writer.getId())
                .getId();
        Throwable thrown = catchThrowable(() -> commentService.deleteComment(commentId, other.getId()));
        assertThat(thrown).isInstanceOf(CommentDomainException.class)
                .hasMessageContaining("삭제 권한이 없습니다");
    }

    @Test
    @DisplayName("관리자는 다른 사용자의 댓글도 삭제할 수 있어야 한다")
    @Transactional
    void 관리자_댓글_삭제() {
        UUID commentId = commentService.addComment(post.getId(), new CommentRequest(null, "부모"), writer.getId())
                .getId();
        commentService.deleteComment(commentId, admin.getId());
        assertThat(commentRepository.findById(commentId)).isNotPresent();
    }

    @Test
    @DisplayName("특정 게시글의 댓글 리스트가 정상적으로 조회되어야 한다")
    @Transactional
    void 댓글_리스트_조회() {
        commentService.addComment(post.getId(), new CommentRequest(null, "c1"), writer.getId());
        commentService.addComment(post.getId(), new CommentRequest(null, "c2"), other.getId());

        List<CommentResponse> result = commentService.getCommentsByPostId(post.getId());
        assertThat(result).hasSize(2)
                .extracting(CommentResponse::getContent)
                .containsExactlyInAnyOrder("c1", "c2");
    }

    @TestConfiguration
    static class MockConfig {
        @Bean NotificationUseCase notificationUseCase() { return mock(NotificationUseCase.class); }
    }
}
