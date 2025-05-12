package org.nova.backend.notification.domain.model.entity.valueobject;

import lombok.Getter;

@Getter
public enum EventType {
    COMMENT("댓글이 달렸습니다."),
    REPLY("답글이 달렸습니다."),
    SUGGESTION_ANSWERED("건의함에 답변이 달렸습니다."),
    POST_LIKE("게시글에 좋아요가 눌렸습니다."),
    NOTIFICATION("공지사항이 달렸습니다.");

    private final String messageTemplate;

    EventType(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
}