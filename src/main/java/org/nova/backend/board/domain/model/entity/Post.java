package org.nova.backend.board.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.domain.model.valueobject.Content;
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.nova.backend.board.domain.model.valueobject.Title;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class Post {
    @Id
    private UUID post_id;

    private UUID member_id;

    @Enumerated(EnumType.STRING)
    private PostType dtype;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    private int view_count;

    private int like_count;

    private int comment_count;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;

    private LocalDateTime created_time;

    private LocalDateTime modified_time;
}
