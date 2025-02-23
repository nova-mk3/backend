package org.nova.backend.board.common.adapter.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.persistence.repository.BoardRepository;
import org.nova.backend.board.common.application.port.out.BoardPersistencePort;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardPersistenceAdapter implements BoardPersistencePort {
    private final BoardRepository boardRepository;

    @Override
    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    @Override
    public Optional<Board> findById(UUID boardId) {
        return boardRepository.findById(boardId);
    }
}

