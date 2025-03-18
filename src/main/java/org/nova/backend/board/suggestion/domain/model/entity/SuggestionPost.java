package org.nova.backend.board.suggestion.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.member.domain.model.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suggestion_post", indexes = {
        @Index(name = "idx_member_id", columnList = "member_id"),
        @Index(name = "idx_created_time", columnList = "createdTime DESC"),
        @Index(name = "idx_is_answered", columnList = "isAnswered, isAdminRead")
})
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
    private boolean isPrivate;

    @Column(nullable = false)
    private boolean isAnswered; // 관리자가 답변했는지 여부

    @Column
    private boolean isAdminRead; // 관리자가 게시글 읽음 여부

    @Column(length = 5000)
    private String adminReply; // 관리자의 답변

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column
    private LocalDateTime adminReplyTime;

    @OneToMany(mappedBy = "suggestionPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuggestionFile> files = new ArrayList<>();

    public void addFiles(List<SuggestionFile> files) {
        for (SuggestionFile file : files) {
            if (!this.files.contains(file)) {
                this.files.add(file);
            }
        }
    }

    public void addAdminReply(String reply) {
        this.adminReply = reply;
        this.isAnswered = true;
        this.adminReplyTime = LocalDateTime.now();
    }

    public void setAdminRead(boolean isAdminRead) {
        this.isAdminRead = isAdminRead;
    }
}