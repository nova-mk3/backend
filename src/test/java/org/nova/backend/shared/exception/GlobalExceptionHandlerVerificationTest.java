package org.nova.backend.shared.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nova.backend.auth.UnauthorizedException;
import org.nova.backend.board.common.adapter.web.CommentController;
import org.nova.backend.board.common.adapter.web.FileController;
import org.nova.backend.board.common.application.dto.request.UpdateCommentRequest;
import org.nova.backend.board.common.application.port.in.CommentUseCase;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.exception.CommentDomainException;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.nova.backend.board.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerVerificationTest {

    @Mock
    private CommentUseCase commentUseCase;

    @Mock
    private FileUseCase fileUseCase;

    @Mock
    private SecurityUtil securityUtil;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        CommentController commentController = new CommentController(commentUseCase, securityUtil);
        FileController fileController = new FileController(fileUseCase, securityUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(
                        commentController,
                        fileController,
                        new TestExceptionController()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void boardDomainException_shouldReturnConfigured404() throws Exception {
        UUID postId = UUID.randomUUID();

        when(commentUseCase.getCommentsByPostId(postId))
                .thenThrow(new BoardDomainException("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void commentDomainException_shouldReturnConfigured403() throws Exception {
        UUID commentId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        when(securityUtil.getCurrentMemberId()).thenReturn(memberId);
        when(commentUseCase.updateComment(any(UUID.class), any(UpdateCommentRequest.class), any(UUID.class)))
                .thenThrow(new CommentDomainException("자신의 댓글만 수정할 수 있습니다.", HttpStatus.FORBIDDEN));

        mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"updated\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("자신의 댓글만 수정할 수 있습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void fileDomainException_shouldReturnConfigured403() throws Exception {
        UUID fileId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        when(securityUtil.getCurrentMemberId()).thenReturn(memberId);
        org.mockito.Mockito.doThrow(new FileDomainException("게시글 작성자만 파일을 삭제할 수 있습니다.", HttpStatus.FORBIDDEN))
                .when(fileUseCase).deleteFileById(fileId, memberId);

        mockMvc.perform(delete("/api/v1/files/{fileId}", fileId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("게시글 작성자만 파일을 삭제할 수 있습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void unauthorizedException_shouldReturn403() throws Exception {
        mockMvc.perform(get("/test-exceptions/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void genericException_shouldReturnGeneric500Message() throws Exception {
        mockMvc.perform(get("/test-exceptions/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @RestController
    @RequestMapping("/test-exceptions")
    static class TestExceptionController {

        @GetMapping("/forbidden")
        List<String> forbidden() {
            throw new UnauthorizedException("권한이 없습니다.");
        }

        @GetMapping("/generic")
        HttpServletResponse generic() {
            throw new RuntimeException("boom");
        }
    }
}
