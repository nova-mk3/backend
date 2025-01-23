package org.nova.backend.board.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.nova.backend.board.domain.model.valueobject.PostType;

@Entity
@Table(name = "post")
public class Post {
    @Id
    private UUID post_id;

    private UUID member_id;

    @Enumerated(EnumType.STRING)
    private PostType dtype;

    private String title;

    private String content;

    private int view_count;

    private int like_count;

    private int comment_count;

    private LocalDateTime created_time;

    private LocalDateTime modified_time;

}
