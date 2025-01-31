package org.nova.backend.board.application.port.in;

import java.util.List;
import org.nova.backend.board.domain.model.entity.Board;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;

public interface BoardUseCase {
    List<Board> getAllBoards();
    Board getBoardByCategory(BoardCategory category);
}