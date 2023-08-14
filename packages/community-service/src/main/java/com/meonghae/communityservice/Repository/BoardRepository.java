package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Slice<Board> findByType(BoardType type, Pageable pageable);
    void deleteAllByEmail(String email);
}
