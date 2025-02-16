package org.nova.backend.board.clubArchive.application.port.out;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.clubArchive.domain.model.entity.JokboPost;
import org.nova.backend.board.clubArchive.domain.model.valueobject.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JokboPostPersistencePort {
    JokboPost save(JokboPost jokboPost);
    Optional<JokboPost> findByPostId(UUID postId);
    Optional<JokboPost> findByPost(Post post);
    void deleteByPost(Post post);
    Page<JokboPost> findPostsByFilter(UUID boardId, String professorName, Integer year, Semester semester, Pageable pageable);
}