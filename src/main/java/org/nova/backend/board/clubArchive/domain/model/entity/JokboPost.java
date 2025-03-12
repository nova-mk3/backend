package org.nova.backend.board.clubArchive.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
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
import org.nova.backend.board.clubArchive.domain.model.valueobject.Semester;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jokbo_post", indexes = {
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_professor_year", columnList = "professorName, year"),
        @Index(name = "idx_subject", columnList = "subject")
})
public class JokboPost {
    @Id
    @Column
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String professorName;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private String subject;

    public void updateJokbo(
            String professorName,
            int year,
            Semester semester,
            String subject
    ) {
        this.professorName = professorName;
        this.year = year;
        this.semester = semester;
        this.subject = subject;
    }
}
