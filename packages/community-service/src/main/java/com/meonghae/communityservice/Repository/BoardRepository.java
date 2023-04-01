package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
