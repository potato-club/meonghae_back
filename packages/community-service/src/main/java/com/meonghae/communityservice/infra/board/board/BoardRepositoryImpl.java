package com.meonghae.communityservice.infra.board.board;

import com.meonghae.communityservice.application.board.port.BoardRepository;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository boardJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Board> findById(Long id) {
        return boardJpaRepository.findById(id).map(BoardEntity::toModel);
    }

    @Override
    public Slice<Board> findByType(BoardType type, Pageable pageable) {
        return boardJpaRepository.findByType(type, pageable).map(BoardEntity::toModel);
    }

    @Override
    public List<Board> findBoardListForMain(LocalDateTime now) {
        QBoardEntity qBoard = QBoardEntity.boardEntity;
        LocalDateTime yesterday = now.minusDays(1).toLocalDate().atStartOfDay();

        return Arrays.stream(BoardType.values()).map(type -> jpaQueryFactory.selectFrom(qBoard)
                .where(qBoard.type.eq(type), qBoard.createdDate.between(yesterday, now))
                .orderBy(qBoard.likes.desc(), qBoard.createdDate.desc())
                .limit(1)
                .fetchOne())
                .filter(Objects::nonNull)
                .map(BoardEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Board save(Board board) {
        return boardJpaRepository.save(BoardEntity.fromModel(board)).toModel();
    }

    @Override
    public void delete(Long id) {
        boardJpaRepository.deleteById(id);
    }
}
