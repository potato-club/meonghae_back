package com.meonghae.communityservice.application.board.port;

import com.meonghae.communityservice.domain.board.BoardComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface CommentRepository {
    Optional<BoardComment> findById(Long commentId);
    Slice<BoardComment> findByBoard_IdAndParentIsNull(Pageable pageable, Long boardId);

    Slice<BoardComment> findByParent_Id(Pageable pageable, Long parentId);

    BoardComment save(BoardComment comment);

    void delete(BoardComment comment);
}
