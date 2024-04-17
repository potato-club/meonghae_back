package com.meonghae.communityservice.infra.board.comment;

import com.meonghae.communityservice.application.board.port.CommentRepository;
import com.meonghae.communityservice.domain.board.BoardComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    private final JPAQueryFactory jpaQueryFactory;

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
    public Map<Long, Long> findCommentCountByBoardIds(List<Long> boardIds) {
        return jpaQueryFactory.select(QCommentEntity.commentEntity.board.id, QCommentEntity.commentEntity.count())
                .from(QCommentEntity.commentEntity)
                .where(QCommentEntity.commentEntity.board.id.in(boardIds))
                .groupBy(QCommentEntity.commentEntity.board.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(QCommentEntity.commentEntity.board.id),
                        tuple -> Optional.ofNullable(tuple.get(QCommentEntity.commentEntity.count())).orElse(0L)
                ));
    }

    @Override
    public BoardComment save(BoardComment comment) {
        return commentJpaRepository.save(CommentEntity.fromModel(comment)).toModel();
    }

    @Override
    public BoardComment saveChild(BoardComment parent, BoardComment child) {
        CommentEntity parentEntity = CommentEntity.fromModel(parent);
        CommentEntity childEntity = CommentEntity.fromModel(child);

        childEntity.setParent(parentEntity);
        CommentEntity saveChild = commentJpaRepository.save(childEntity);

        parentEntity.addReply(saveChild);
        commentJpaRepository.save(parentEntity);

        return saveChild.toModel();
    }

    @Override
    public void delete(Long id) {
        commentJpaRepository.deleteById(id);
    }

    @Override
    public int countByBoardId(Long boardId) {
        return commentJpaRepository.countByBoard_Id(boardId);
    }
}
