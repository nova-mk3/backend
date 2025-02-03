package org.nova.backend.board.adapter.persistence.init;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.adapter.persistence.repository.BoardRepository;
import org.nova.backend.board.domain.model.entity.Board;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BoardInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(BoardInitializer.class);
    private final BoardRepository boardRepository;

    public BoardInitializer(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<BoardCategory> categories = Arrays.asList(
                BoardCategory.INTEGRATED,
                BoardCategory.CLUB_ARCHIVE,
                BoardCategory.SUGGESTION
        );

        for (BoardCategory category : categories) {
            boardRepository.findByCategory(category).ifPresentOrElse(
                    existingBoard -> logger.info("[{}] 게시판이 이미 존재합니다.", category),
                    () -> {
                        Board newBoard = new Board(UUID.randomUUID(), category);
                        boardRepository.save(newBoard);
                        logger.info("[{}] 게시판이 생성되었습니다. ID: {}", category, newBoard.getId());
                    }
            );
        }
    }
}
