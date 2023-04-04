package com.meonghae.communityservice.Entity.Board;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board_comments")
public class BoardComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String comment;
    @Column(nullable = false)
    private String userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BoardComment parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<BoardComment> replies = new ArrayList<>();

    @Column(nullable = false)
    private Boolean update;

    public void addReply(BoardComment reply) {
        replies.add(reply);
        reply.setParent(this);
    }

    public boolean isParent() {
        return this.parent == null;
    }

    private void setParent(BoardComment parent) {
        this.parent = parent;
    }

    public void updateComment(String newComment) {
        this.comment = newComment;
        this.update = true;
    }
}
