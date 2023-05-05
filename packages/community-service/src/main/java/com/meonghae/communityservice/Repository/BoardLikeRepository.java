package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    BoardLike findByEmailAndBoard(String email, Board board);
}
