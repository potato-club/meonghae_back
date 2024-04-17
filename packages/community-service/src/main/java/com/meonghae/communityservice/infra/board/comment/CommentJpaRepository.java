package com.meonghae.communityservice.infra.board.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
    Slice<CommentEntity> findByBoard_IdAndParentIsNull(Pageable pageable, Long boardId);

    Slice<CommentEntity> findByParent_Id(Pageable pageable, Long parentId);

    int countByBoard_Id(Long boardId);
}
