package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Slice<Board> findByType(BoardType type, Pageable pageable);

    // QueryDSL 로 수정하기
    @Query("select b from Board b where b.likes = "
    + "(select MAX (l.likes) from Board l where l.type = b.type) group by b.type")
    List<Board> findTop1LikedPerType();
}
