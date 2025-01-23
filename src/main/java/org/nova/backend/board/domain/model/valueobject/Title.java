package org.nova.backend.board.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import org.nova.backend.board.domain.exception.BoardDomainException;

@Embeddable
public final class Title {
    private final String value;

    protected Title() {
        this.value = ""; // JPA 기본 생성자
    }

    public Title(String value){
        if(value == null || value.trim().isEmpty()){
            throw  new BoardDomainException("제목은 비어 있을 수 없습니다.");
        }
        if(value.length() > 255) {
            throw new BoardDomainException("제목은 255자를 초과할 수 없습니다.");
        }
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
