package org.nova.backend.board.common.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nova.backend.board.common.application.port.out.BoardPersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.nova.backend.annotation.FastTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@FastTest
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardPersistencePort boardPersistencePort;

    @InjectMocks
    private BoardService boardService;

    private UUID boardId1;
    private UUID boardId2;
    private Board board1;
    private Board board2;

    @BeforeEach
    void setUp() {
        boardId1 = UUID.randomUUID();
        boardId2 = UUID.randomUUID();
        board1 = new Board(boardId1, BoardCategory.INTEGRATED);
        board2 = new Board(boardId2, BoardCategory.CLUB_ARCHIVE);
    }

    @Test
    @DisplayName("모든 게시판을 조회할 수 있다")
    void 게시판_전체_조회() {
        when(boardPersistencePort.findAllBoards()).thenReturn(List.of(board1, board2));

        List<Board> boards = boardService.getAllBoards();

        assertThat(boards)
                .hasSize(2)
                .containsExactlyInAnyOrder(board1, board2);
    }

    @Test
    @DisplayName("게시판 ID로 특정 게시판을 조회할 수 있다")
    void 게시판_ID로_조회() {
        when(boardPersistencePort.findById(boardId1)).thenReturn(Optional.of(board1));

        Board foundBoard = boardService.getBoardById(boardId1);

        assertThat(foundBoard)
                .isNotNull()
                .extracting(Board::getId)
                .isEqualTo(boardId1);

        assertThat(foundBoard.getCategory())
                .isEqualTo(BoardCategory.INTEGRATED);
    }

    @Test
    @DisplayName("존재하지 않는 게시판 ID로 조회 시 예외가 발생한다")
    void 존재하지_않는_게시판_조회시_예외발생() {
        UUID randomBoardId = UUID.randomUUID();
        when(boardPersistencePort.findById(randomBoardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.getBoardById(randomBoardId))
                .isInstanceOf(BoardDomainException.class)
                .hasMessageContaining("게시판을 찾을 수 없습니다. ID: " + randomBoardId);
    }
}
