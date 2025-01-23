package org.nova.backend.board.domain.model.valueobject;

import jakarta.persistence.Embeddable;

@Embeddable
public final class Content {
    private final String value;

    protected Content(){
        this.value = "";
    }

    public Content(String value){
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
        }
        if (value.length() > 5000) {
            throw new IllegalArgumentException("내용은 5000자를 초과할 수 없습니다.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
