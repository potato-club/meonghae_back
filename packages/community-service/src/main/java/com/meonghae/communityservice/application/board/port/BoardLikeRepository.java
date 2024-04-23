package com.meonghae.communityservice.application.board.port;

import com.meonghae.communityservice.domain.board.BoardLike;

public interface BoardLikeRepository {
    BoardLike findByEmailAndBoardEntity_Id(String email, Long boardId);

    BoardLike save(BoardLike newLike);
}
