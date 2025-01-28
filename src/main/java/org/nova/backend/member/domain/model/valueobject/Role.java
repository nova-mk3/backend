package org.nova.backend.member.domain.model.valueobject;

public enum Role {
    GENERAL("부원"),
    CHAIRMAN("회장"),
    VICE_CHAIRMAN("부회장"),
    EXECUTIVE("임원"),
    ADMINISTRATOR("관리자");

    private final String type;

    Role(String type) {
        this.type = type;
    }
}
