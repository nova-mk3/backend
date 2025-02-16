package org.nova.backend.board.suggestion.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.member.domain.model.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suggestion_post")
public class SuggestionPost {
    @Id
    @Column
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false)
    private boolean isRead; // 관리자가 읽었는지 여부

    @Column(nullable = false)
    private boolean isAnswered; // 관리자가 답변했는지 여부

    @Column(nullable = false)
    private boolean isAnswerRead; // 사용자가 답변을 읽었는지 여부

    @Column(nullable = false)
    private boolean isPrivate;

    @Column
    private String adminReply; // 관리자의 답변

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime modifiedTime;

    public void markAsRead() {
        this.isRead = true;
    }

    public void addAdminReply(String reply) {
        this.adminReply = reply;
        this.isAnswered = true;
        this.isAnswerRead = false;
    }

    public void markAnswerAsRead() {
        this.isAnswerRead = true;
    }
}