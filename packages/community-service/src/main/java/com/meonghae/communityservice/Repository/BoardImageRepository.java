package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Board.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
}
