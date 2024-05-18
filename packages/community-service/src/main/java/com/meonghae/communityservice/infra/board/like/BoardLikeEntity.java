package com.meonghae.communityservice.infra.board.like;


import com.meonghae.communityservice.domain.board.BoardLike;
import com.meonghae.communityservice.infra.board.board.BoardEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_like")
public class BoardLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BoardEntity boardEntity;

    @Column(nullable = false)
    private Boolean status;

    public static BoardLikeEntity fromModel(BoardLike like) {
        BoardLikeEntity entity = new BoardLikeEntity();
        entity.id = like.getId();
        entity.boardEntity = BoardEntity.fromModel(like.getBoard());
        entity.email = like.getEmail();
        entity.status = like.getStatus();

        return entity;
    }

    public BoardLike toModel() {
        return BoardLike.builder()
                .id(this.getId())
                .board(this.getBoardEntity().toModel())
                .email(this.getEmail())
                .status(this.getStatus())
                .build();
    }
}
