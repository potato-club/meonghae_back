package com.meonghae.communityservice.infra.board.board;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.infra.BaseTimeEntity;
import com.meonghae.communityservice.domain.board.BoardType;
import com.meonghae.communityservice.infra.board.comment.CommentEntity;
import lombok.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "board")
public class BoardEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column
    @Enumerated(value = EnumType.STRING)
    private BoardType type;
    private int likes;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean hasImage;
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<CommentEntity> comments;

    public static BoardEntity fromModel(Board board) {
        BoardEntity entity = new BoardEntity();
        entity.id = board.getId();
        entity.email = board.getEmail();
        entity.title = board.getTitle();
        entity.content = board.getContent();
        entity.type = board.getType();
        entity.likes = board.getLikes();
        entity.hasImage = board.getHasImage();
        entity.comments = CollectionUtils.isEmpty(board.getComments()) ? new ArrayList<>() :
                board.getComments().stream().map(CommentEntity::fromModel).collect(Collectors.toList());

        return entity;
    }

    public Board toModel() {
        return Board.builder()
                .id(this.id)
                .comments(this.comments.stream().map(CommentEntity::toModel).collect(Collectors.toList()))
                .email(this.email)
                .content(this.content)
                .title(this.title)
                .type(this.type)
                .likes(this.likes)
                .hasImage(this.hasImage)
                .build();
    }
}
