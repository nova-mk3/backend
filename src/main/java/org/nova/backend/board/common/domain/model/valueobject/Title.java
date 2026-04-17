package org.nova.backend.board.common.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.springframework.http.HttpStatus;

@Getter
@Embeddable
public final class Title {
    private final String title;

    protected Title() {
        this.title = "";
    }

    public Title(String title){
        if(title == null || title.trim().isEmpty()){
            throw  new BoardDomainException("제목은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if(title.length() > 255) {
            throw new BoardDomainException("제목은 255자를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        this.title = title;
    }

}
