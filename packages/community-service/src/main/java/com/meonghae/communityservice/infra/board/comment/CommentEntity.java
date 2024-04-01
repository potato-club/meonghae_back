package com.meonghae.communityservice.infra.board.comment;

import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.infra.BaseTimeEntity;
import com.meonghae.communityservice.infra.board.board.BoardEntity;
import lombok.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "board_comments")
public class CommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CommentEntity> replies;

    @Column(nullable = false)
    private Boolean updated;

    public static CommentEntity fromModel(BoardComment comment) {
        CommentEntity entity = new CommentEntity();
        entity.id = comment.getId();
        entity.comment = comment.getComment();
        entity.email = comment.getEmail();
        entity.board = BoardEntity.fromModel(comment.getBoard());
        entity.parent = comment.getParent() == null ? null : fromModel(comment.getParent());
        entity.replies = CollectionUtils.isEmpty(comment.getReplies()) ?
                new ArrayList<>() : comment.getReplies().stream()
                .map(CommentEntity::fromModel).collect(Collectors.toList());
        entity.updated = comment.getUpdated();
        return entity;
    }

    public BoardComment toModel() {
        return BoardComment.builder()
                .id(this.id)
                .email(this.email)
                .comment(this.comment)
                .updated(this.updated)
                .board(this.board.toModel())
                .parent(this.parent.toModel())
                .replies(this.replies.stream().map(CommentEntity::toModel).collect(Collectors.toList()))
                .build();
    }
}
