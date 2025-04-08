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
        @Index(name = "idx_board_id", columnList = "board_id"),
        @Index(name = "idx_board_type", columnList = "board_id, post_type"),
        @Index(name = "idx_board_type_created", columnList = "board_id, post_type, created_time DESC"),
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

    @Column(nullable = false)
    private int totalDownloadCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime modifiedTime;

    public void updatePost(PostType postType, String title, String content) {
        this.postType = postType;
        this.title = title;
        this.content = content;
        this.modifiedTime = LocalDateTime.now();
    }

    public void addFiles(List<File> files) {
        for (File file : files) {
            if (!this.files.contains(file)) {
                this.files.add(file);
            }
        }
    }
    public void removeFilesByIds(List<UUID> fileIds) {
        this.files.removeIf(file -> fileIds.contains(file.getId()));
    }
    public void incrementCommentCount() {
        this.commentCount += 1;
    }

    public void decrementCommentCount(int count) {
        this.commentCount = Math.max(0, this.commentCount - count);
    }

    public void incrementTotalDownloadCount() {
        this.totalDownloadCount += 1;
    }
}


