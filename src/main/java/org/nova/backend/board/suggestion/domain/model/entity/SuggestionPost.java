package org.nova.backend.board.suggestion.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
    private boolean isPrivate;

    @Column(nullable = false)
    private boolean isAnswered; // 관리자가 답변했는지 여부

    @Column(nullable = false)
    private boolean isAnswerRead; // 사용자가 답변을 읽었는지 여부

    @Column(length = 5000)
    private String adminReply; // 관리자의 답변

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime modifiedTime;

    @OneToMany(mappedBy = "suggestionPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuggestionFile> files = new ArrayList<>();

    public void addFiles(List<SuggestionFile> files) {
        this.files.addAll(files);
    }

    public void addAdminReply(String reply) {
        this.adminReply = reply;
        this.isAnswered = true;
        this.isAnswerRead = false;
        this.modifiedTime = LocalDateTime.now();
    }

    public void markAnswerAsRead() {
        this.isAnswerRead = true;
        this.modifiedTime = LocalDateTime.now();
    }
}