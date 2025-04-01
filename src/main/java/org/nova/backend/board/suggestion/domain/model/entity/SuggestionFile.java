package org.nova.backend.board.suggestion.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suggestion_file", indexes = {
        @Index(name = "idx_suggestion_post_id", columnList = "suggestion_post_id"),
        @Index(name = "idx_file_path", columnList = "filePath")
})
public class SuggestionFile {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String filePath;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggestion_post_id")
    @JsonIgnore
    private SuggestionPost suggestionPost;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestionFile file = (SuggestionFile) o;
        return Objects.equals(id, file.id) &&
                Objects.equals(originalFilename, file.originalFilename) &&
                Objects.equals(filePath, file.filePath);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originalFilename, filePath);
    }
}
