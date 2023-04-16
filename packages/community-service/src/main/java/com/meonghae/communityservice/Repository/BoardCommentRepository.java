package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    Slice<BoardComment> findByBoardAndParentIsNull(Pageable pageable, Board board);

    Slice<BoardComment> findByParent(Pageable pageable, BoardComment parent);
}
