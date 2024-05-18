package com.meonghae.communityservice.infra.board.board;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.infra.BaseTimeEntity;
import com.meonghae.communityservice.domain.board.BoardType;
import lombok.*;

import javax.persistence.*;

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

    public static BoardEntity fromModel(Board board) {
        return BoardEntity.builder()
                .id(board.getId())
                .email(board.getEmail())
                .title(board.getTitle())
                .content(board.getContent())
                .type(board.getType())
                .likes(board.getLikes())
                .hasImage(board.getHasImage())
                .build();
    }

    public Board toModel() {
        return Board.builder()
                .id(this.getId())
                .email(this.getEmail())
                .content(this.getContent())
                .title(this.getTitle())
                .type(this.getType())
                .likes(this.getLikes())
                .hasImage(this.getHasImage())
                .createdDate(this.getCreatedDate())
                .modifiedDate(this.getModifiedDate())
                .build();
    }

    public void updateEntity(Board board) {
        this.title = board.getTitle();
        this.content = board.getContent();
        this.hasImage = board.getHasImage();
    }
}
