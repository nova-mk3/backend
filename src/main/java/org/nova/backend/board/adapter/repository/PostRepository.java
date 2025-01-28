package org.nova.backend.board.adapter.repository;

import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
}
