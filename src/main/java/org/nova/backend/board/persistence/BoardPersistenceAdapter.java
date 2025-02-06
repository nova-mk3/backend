package org.nova.backend.board.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.persistence.repository.BoardRepository;
import org.nova.backend.board.common.application.port.out.BoardPersistencePort;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.springframework.stereotype.Component;

@Component
public class BoardPersistenceAdapter implements BoardPersistencePort {
    private final BoardRepository boardRepository;

    public BoardPersistenceAdapter(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    @Override
    public Optional<Board> findByCategory(BoardCategory category) {
        return boardRepository.findByCategory(category);
    }

    @Override
    public Optional<Board> findById(UUID boardId) {
        return boardRepository.findById(boardId);
    }
}

