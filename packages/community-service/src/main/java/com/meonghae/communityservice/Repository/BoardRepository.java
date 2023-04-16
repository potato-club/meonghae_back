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

    // QueryDSL 로 수정하기 -> 메인페이지 요청 많음 + 쿼리 3개씩 날림 개손해
    @Query("SELECT b FROM Board b WHERE (b.type, b.likes) IN "
            + "(SELECT b2.type, MAX(b2.likes) FROM Board b2 GROUP BY b2.type HAVING MAX(b2.likes) >= 0) AND b.createdDate = "
            + "(SELECT MAX(b3.createdDate) FROM Board b3 WHERE b3.type = b.type AND b3.likes = b.likes)")
    List<Board> findTop1LikedPerType();
}
