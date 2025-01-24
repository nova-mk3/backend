package org.nova.backend.board.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public final class Content {
    private final String content;

    protected Content(){
        this.content = "";
    }

    public Content(String content){
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
        }
        if (content.length() > 5000) {
            throw new IllegalArgumentException("내용은 5000자를 초과할 수 없습니다.");
        }
        this.content = content;
    }

}
