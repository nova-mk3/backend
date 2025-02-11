package org.nova.backend.board.examarchive.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.examarchive.domain.model.valueobject.Semester;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jokbo_post")
public class JokboPost {
    @Id
    @Column
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String ProfessorName;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private String subject;
}
