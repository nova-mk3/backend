package org.nova.backend.board.common.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.application.port.out.BoardPersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService implements BoardUseCase {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private final BoardPersistencePort boardPersistencePort;

    /**
     * 모든 게시판 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        logger.info("모든 게시판 조회 요청");
        return boardPersistencePort.findAllBoards();
    }

    /**
     * 특정 게시판 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Board getBoardById(UUID boardId) {
        logger.info("게시판 조회 요청 - ID: {}", boardId);
        return boardPersistencePort.findById(boardId)
                .orElseThrow(() -> {
                    logger.warn("게시판을 찾을 수 없습니다. ID: {}", boardId);
                    return new BoardDomainException("게시판을 찾을 수 없습니다. ID: " + boardId);
                });
    }
}
