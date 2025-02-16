package org.nova.backend.board.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.clubArchive.domain.model.entity.JokboPost;
import org.nova.backend.board.clubArchive.domain.model.valueobject.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JokboPostRepository extends JpaRepository<JokboPost, UUID> {
    Optional<JokboPost> findById(UUID id);
    void deleteByPost(Post post);

    @Query("SELECT jp FROM JokboPost jp WHERE jp.post.id = :postId")
    Optional<JokboPost> findByPostId(@Param("postId") UUID postId);

    @Query("SELECT jp FROM JokboPost jp " +
            "JOIN jp.post p " +
            "WHERE p.board.id = :boardId " +
            "AND (:professorName IS NULL OR jp.professorName LIKE %:professorName%) " +
            "AND (:year IS NULL OR jp.year = :year) " +
            "AND (:semester IS NULL OR jp.semester = :semester)")
    Page<JokboPost> findByFilter(
            @Param("boardId") UUID boardId,
            @Param("professorName") String professorName,
            @Param("year") Integer year,
            @Param("semester") Semester semester,
            Pageable pageable
    );
}
