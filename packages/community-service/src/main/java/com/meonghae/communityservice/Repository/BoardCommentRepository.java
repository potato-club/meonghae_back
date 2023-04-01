package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
}
