package com.meonghae.communityservice.infra.board.board;

import com.meonghae.communityservice.domain.board.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {
    Slice<BoardEntity> findByType(BoardType type, Pageable pageable);
    void deleteAllByEmail(String email);
}
