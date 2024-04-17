package com.meonghae.communityservice.application.board.port;

import com.meonghae.communityservice.domain.board.BoardComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepository {
    Optional<BoardComment> findById(Long commentId);

    Slice<BoardComment> findByBoard_IdAndParentIsNull(Pageable pageable, Long boardId);

    Slice<BoardComment> findByParent_Id(Pageable pageable, Long parentId);

    Map<Long, Long> findCommentCountByBoardIds(List<Long> boardIds);

    BoardComment save(BoardComment comment);

    BoardComment saveChild(BoardComment parent, BoardComment child);

    void delete(Long commentId);

    int countByBoardId(Long boardId);
}
