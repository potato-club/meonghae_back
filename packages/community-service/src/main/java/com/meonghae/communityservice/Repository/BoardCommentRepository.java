package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    // board 의 부모댓글만 가져오기 -> 나중에 페이징처리
    List<BoardComment> findByBoardAndParentIsNull(Board board);

    // parent 에 대한 자식댓글 리스트 가져오기 -> 이것도 나중에 페이징처리
    List<BoardComment> findByParent(BoardComment parent);
}
