package org.nova.backend.member.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProfilePhoto {
    @Id
    @Column(name = "profile_photo_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String originalFilename;

    @Column
    private String filePath;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfilePhoto file = (ProfilePhoto) o;
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


