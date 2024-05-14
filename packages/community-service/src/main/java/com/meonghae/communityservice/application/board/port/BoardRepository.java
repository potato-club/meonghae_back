package com.meonghae.communityservice.application.board.port;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    Optional<Board> findById(Long id);
    Slice<Board> findByType(BoardType type, Pageable pageable);
    List<Board> findBoardListForMain(LocalDateTime now);
    Board save(Board board);
    void delete(Long id);
    Board update(Board updateBoard);
}
