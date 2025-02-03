package org.nova.backend.board.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Board;

public interface BoardUseCase {
    List<Board> getAllBoards();
    Board getBoardById(UUID boardId);
}