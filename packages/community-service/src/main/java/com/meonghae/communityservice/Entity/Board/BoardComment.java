package com.meonghae.communityservice.Entity.Board;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public void addReply(BoardComment reply) {
        replies.add(reply);
        reply.setParent(this);
    }

    private void setParent(BoardComment parent) {
        this.parent = parent;
    }
}
