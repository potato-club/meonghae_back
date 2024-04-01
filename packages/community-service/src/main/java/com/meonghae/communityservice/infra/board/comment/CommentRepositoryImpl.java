package com.meonghae.communityservice.infra.board.comment;

import com.meonghae.communityservice.application.board.port.CommentRepository;
import com.meonghae.communityservice.domain.board.BoardComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Optional<BoardComment> findById(Long commentId) {
        return commentJpaRepository.findById(commentId).map(CommentEntity::toModel);
    }

    @Override
    public Slice<BoardComment> findByBoard_IdAndParentIsNull(Pageable pageable, Long boardId) {
        return commentJpaRepository.findByBoard_IdAndParentIsNull(pageable, boardId)
                .map(CommentEntity::toModel);
    }

    @Override
    public Slice<BoardComment> findByParent_Id(Pageable pageable, Long parentId) {
        return commentJpaRepository.findByParent_Id(pageable, parentId)
                .map(CommentEntity::toModel);
    }

    @Override
    public BoardComment save(BoardComment comment) {
        return commentJpaRepository.save(CommentEntity.fromModel(comment)).toModel();
    }

    @Override
    public void delete(BoardComment comment) {
        commentJpaRepository.delete(CommentEntity.fromModel(comment));
    }
}
