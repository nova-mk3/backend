package org.nova.backend.board.common.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory category;
}
