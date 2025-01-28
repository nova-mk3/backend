//package org.nova.backend.board.domain.model.entity;
//
//import jakarta.persistence.AttributeOverride;
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Embedded;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.nova.backend.board.domain.model.valueobject.Content;
//
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "comment")
//public class Comment {
//    @Id
//    @Column
//    private UUID commentId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "postId", nullable = false)
//    private Post post;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_comment_id")
//    private Comment parentComment;
//
//    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> replies;
//
//    @Embedded
//    private Content content;
//
//    @Column
//    private LocalDateTime createdTime;
//
//    @Column
//    private LocalDateTime modifiedTime;
//}
