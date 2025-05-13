package org.nova.backend.notification.domain.model.entity.valueobject;

import lombok.Getter;

@Getter
public enum EventType {
    COMMENT("댓글"),
    REPLY("답글"),
    SUGGESTION_ANSWERED("건의 게시글 답변"),
    POST_LIKE("좋아요"),
    NOTIFICATION("공지사항");

    private final String messageTemplate;

    EventType(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
}