package org.nova.backend.board.common.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.member.domain.model.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", indexes = {
        @Index(name = "idx_member_id", columnList = "member_id"),
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_post_type", columnList = "post_type")
})
public class Post {
    @Id
    @Column
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String  content;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int commentCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime modifiedTime;

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
        this.modifiedTime = LocalDateTime.now();
    }

    public void addFiles(List<File> files) {
        this.files.addAll(files);
    }
    public void removeFiles(List<File> filesToRemove) {
        this.files.removeAll(filesToRemove);
    }
    public void incrementCommentCount() {
        this.commentCount += 1;
    }
}


