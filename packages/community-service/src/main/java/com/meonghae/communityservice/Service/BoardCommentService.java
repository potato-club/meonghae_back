package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Dto.CommentDto.*;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardComment;
import com.meonghae.communityservice.Repository.BoardCommentRepository;
import com.meonghae.communityservice.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository commentRepository;

    @Transactional
    public List<CommentParentDto> getParentComments(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("board is not exist"));
        List<BoardComment> comments = commentRepository.findByBoardAndParentIsNull(board);
        return comments.stream().map(CommentParentDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<CommentChildDto> getChildComments(Long parentId) {
        BoardComment parent = commentRepository.findById(parentId).orElse(null);
        if(parent == null || !parent.isParent()) {
            throw new RuntimeException("this comment is not parent");
        }
        List<BoardComment> childComments = commentRepository.findByParent(parent);
        return childComments.stream().map(CommentChildDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void addParentComment(Long boardId, CommentRequestDto requestDto) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("board is not exist"));
        BoardComment parent = createComment(findBoard, requestDto);
        commentRepository.save(parent);
    }

    @Transactional
    public void addChildComment(Long parentId, CommentRequestDto requestDto) {
        BoardComment parent = commentRepository.findById(parentId)
                .orElse(null);
        if(parent == null || !parent.isParent()) {
            throw new RuntimeException("해당 댓글에는 대댓글을 달 수 없습니다.");
        }
        BoardComment child = createComment(parent.getBoard(), requestDto);
        commentRepository.save(child);
        parent.addReply(child);
    }

    @Transactional
    public ReloadCommentDto updateComment(Long id, CommentUpdateDto updateDto) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment is not exist"));
        if(!comment.getUserId().equals(updateDto.getUserId())) {
            throw new RuntimeException("댓글 작성자만 수정 가능합니다.");
        }
        comment.updateComment(updateDto.getComment());
        if(comment.isParent()) {
            return new ReloadCommentDto(comment.getBoard().getId(), comment.isParent());
        }
        return new ReloadCommentDto(comment.getParent().getId(), comment.isParent());
    }

    @Transactional
    public ReloadCommentDto deleteComment(Long id, String userId) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment is not exist"));
        if(!comment.getUserId().equals(userId)) {
            throw new RuntimeException("댓글 작성자만 삭제 가능합니다.");
        }
        commentRepository.delete(comment);
        if(comment.isParent()) {
            return new ReloadCommentDto(comment.getBoard().getId(), comment.isParent());
        }
        return new ReloadCommentDto(comment.getParent().getId(), comment.isParent());
    }

    private BoardComment createComment(Board findBoard, CommentRequestDto requestDto) {
        BoardComment comment = BoardComment.builder()
                .board(findBoard)
                .comment(requestDto.getComment())
                .update(false)
                .userId(requestDto.getUserId()).build();
        return comment;
    }
}
