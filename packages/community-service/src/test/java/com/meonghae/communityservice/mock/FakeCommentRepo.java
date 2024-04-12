package com.meonghae.communityservice.mock;

import com.meonghae.communityservice.application.board.port.CommentRepository;
import com.meonghae.communityservice.domain.board.BoardComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeCommentRepo implements CommentRepository {

    private final AtomicLong autoGeneratedId = new AtomicLong(0L);
    private final List<BoardComment> data = new ArrayList<>();

    @Override
    public Optional<BoardComment> findById(Long commentId) {
       return data.stream().filter(comment -> comment.getId().equals(commentId)).findAny();
    }

    @Override
    public Slice<BoardComment> findByBoard_IdAndParentIsNull(Pageable pageable, Long boardId) {
        return null;
    }

    @Override
    public Slice<BoardComment> findByParent_Id(Pageable pageable, Long parentId) {
        return null;
    }

    @Override
    public BoardComment save(BoardComment comment) {
        if (comment.getId() == null || comment.getId() == 0) {
            BoardComment saveComment = BoardComment.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .email(comment.getEmail())
                    .parent(comment.getParent() == null ? null : comment.getParent())
                    .updated(comment.getUpdated())
                    .replies(comment.getReplies())
                    .createdDate(comment.getCreatedDate())
                    .modifiedDate(comment.getModifiedDate())
                    .comment(comment.getComment())
                    .board(comment.getBoard())
                    .build();

            data.add(saveComment);
            return saveComment;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), comment.getId()));
            data.add(comment);
            return comment;
        }
    }

    @Override
    public void delete(BoardComment comment) {

    }
}