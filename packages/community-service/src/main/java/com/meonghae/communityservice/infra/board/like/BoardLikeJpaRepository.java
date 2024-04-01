package com.meonghae.communityservice.infra.board.like;

import com.meonghae.communityservice.infra.board.board.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeJpaRepository extends JpaRepository<BoardLikeEntity, Long> {
    BoardLikeEntity findByEmailAndBoardEntity_Id(String email, Long boardId);
}
