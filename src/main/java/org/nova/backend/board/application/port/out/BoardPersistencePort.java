package org.nova.backend.board.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Board;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;

public interface BoardPersistencePort {
    Optional<Board> findByCategory(BoardCategory category);
    List<Board> findAllBoards();
    Optional<Board> findById(UUID boardId);
}
