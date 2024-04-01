package com.meonghae.communityservice.infra.board.like;

import com.meonghae.communityservice.application.board.port.BoardLikeRepository;
import com.meonghae.communityservice.domain.board.BoardLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardLikeRepositoryImpl implements BoardLikeRepository {

    private final BoardLikeJpaRepository likeJpaRepository;

    @Override
    public BoardLike findByEmailAndBoardEntity_Id(String email, Long boardId) {
        return likeJpaRepository.findByEmailAndBoardEntity_Id(email, boardId).toModel();
    }

    @Override
    public BoardLike save(BoardLike like) {
        return likeJpaRepository.save(BoardLikeEntity.fromModel(like)).toModel();
    }
}
