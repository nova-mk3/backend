package org.nova.backend.board.examarchive.domain.model.valueobject;

import lombok.Getter;

@Getter
public enum Semester {
    FIRST_SEMESTER("1학기"),
    SECOND_SEMESTER("2학기"),
    SUMMER_SEMESTER("여름 계절학기"),
    WINTER_SEMESTER("겨울 계절학기");

    private final String displayName;

    Semester(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
