package org.nova.backend.board.application.service;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.port.in.BoardUseCase;
import org.nova.backend.board.application.port.out.BoardPersistencePort;
import org.nova.backend.board.domain.exception.BoardDomainException;
import org.nova.backend.board.domain.model.entity.Board;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BoardService implements BoardUseCase {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private final BoardPersistencePort boardPersistencePort;

    public BoardService(BoardPersistencePort boardPersistencePort) {
        this.boardPersistencePort = boardPersistencePort;
    }

    /**
     * 모든 게시판 조회
     */
    @Override
    public List<Board> getAllBoards() {
        logger.info("모든 게시판 조회 요청");
        return boardPersistencePort.findAllBoards();
    }

    /**
     * 특정 게시판 조회
     */
    @Override
    public Board getBoardByCategory(BoardCategory category) {
        return boardPersistencePort.findByCategory(category)
                .orElseThrow(() -> new BoardDomainException("게시판을 찾을 수 없습니다. Category: " + category));
    }

    /**
     * 특정 게시판 조회
     */
    @Override
    public Board getBoardById(UUID boardId) {
        logger.info("게시판 조회 요청 - ID: {}", boardId);
        return boardPersistencePort.findById(boardId)
                .orElseThrow(() -> {
                    logger.warn("게시판을 찾을 수 없습니다. ID: {}", boardId);
                    return new BoardDomainException("게시판을 찾을 수 없습니다. ID: " + boardId);
                });
    }
}
