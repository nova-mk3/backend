package org.nova.backend.board.adapter.persistence;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    @Query("SELECT f FROM File f WHERE f.post.post_id = :postId")
    List<File> findByPostId(@Param("postId") UUID postId);
}
