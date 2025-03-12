package org.nova.backend.board.common.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.Board;

public interface BoardUseCase {
    List<Board> getAllBoards();
    Board getBoardById(UUID boardId);
}