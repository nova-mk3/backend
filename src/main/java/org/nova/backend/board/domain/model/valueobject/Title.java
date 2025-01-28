package org.nova.backend.board.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.nova.backend.board.domain.exception.BoardDomainException;

@Getter
@Embeddable
public final class Title {
    private final String title;

    protected Title() {
        this.title = "";
    }

    public Title(String title){
        if(title == null || title.trim().isEmpty()){
            throw  new BoardDomainException("제목은 비어 있을 수 없습니다.");
        }
        if(title.length() > 255) {
            throw new BoardDomainException("제목은 255자를 초과할 수 없습니다.");
        }
        this.title = title;
    }

}
