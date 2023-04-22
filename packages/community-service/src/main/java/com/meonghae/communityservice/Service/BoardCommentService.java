package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Dto.CommentDto.*;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardComment;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Exception.Custom.CommentException;
import com.meonghae.communityservice.Exception.Error.ErrorCode;
import com.meonghae.communityservice.Repository.BoardCommentRepository;
import com.meonghae.communityservice.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository commentRepository;
    private final RedisService redisService;

    @Transactional
    public Slice<CommentParentDto> getParentComments(int page, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        PageRequest request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "id"));
        Slice<BoardComment> comments = commentRepository.findByBoardAndParentIsNull(request, board);
        Slice<CommentParentDto> dtoPage = comments.map(comment -> {
            String nickname = redisService.getNickname(comment.getUserId());
            return new CommentParentDto(comment, nickname);
        });
        return dtoPage;
    }

    @Transactional
    public Slice<CommentChildDto> getChildComments(int page, Long parentId) {
        BoardComment parent = commentRepository.findById(parentId).orElse(null);
        if(parent == null || !parent.isParent()) {
            throw new CommentException(ErrorCode.BAD_REQUEST, "this comment is not parent");
        }
        PageRequest request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.ASC, "id"));
        Slice<BoardComment> childComments = commentRepository.findByParent(request, parent);
        Slice<CommentChildDto> dtoPage = childComments.map(comment -> {
            String nickname = redisService.getNickname(comment.getUserId());
            return new CommentChildDto(comment, nickname);
        });
        return dtoPage;
    }

    @Transactional
    public void addParentComment(Long boardId, CommentRequestDto requestDto) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        BoardComment parent = createComment(findBoard, requestDto);
        commentRepository.save(parent);
    }

    @Transactional
    public void addChildComment(Long parentId, CommentRequestDto requestDto) {
        BoardComment parent = commentRepository.findById(parentId)
                .orElse(null);
        if(parent == null || !parent.isParent()) {
            throw new CommentException(ErrorCode.BAD_REQUEST, "해당 댓글에는 대댓글을 달 수 없습니다.");
        }
        BoardComment child = createComment(parent.getBoard(), requestDto);
        commentRepository.save(child);
        parent.addReply(child);
    }

    @Transactional
    public String updateComment(Long id, CommentRequestDto updateDto) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment is not exist"));
        if(!comment.getUserId().equals(redisService.getUserId(updateDto.getNickname()))) {
            throw new CommentException(ErrorCode.UNAUTHORIZED, "댓글 작성자만 수정 가능합니다.");
        }
        comment.updateComment(updateDto.getComment());
        if(comment.isParent()) {
            return "부모댓글 수정 완료";
        }
        return "자식댓글 수정 완료";
    }

    @Transactional
    public String deleteComment(Long id, String nickname) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment is not exist"));
        if(!comment.getUserId().equals(redisService.getUserId(nickname))) {
            throw new CommentException(ErrorCode.UNAUTHORIZED, "댓글 작성자만 삭제 가능합니다.");
        }
        commentRepository.delete(comment);
        if(comment.isParent()) {
            return "부모댓글 삭제 완료";
        }
        return "자식댓글 삭제 완료";
    }

    private BoardComment createComment(Board findBoard, CommentRequestDto requestDto) {
        String userId = redisService.getUserId(requestDto.getNickname());
        BoardComment comment = BoardComment.builder()
                .board(findBoard)
                .comment(requestDto.getComment())
                .updated(false)
                .userId(userId).build();
        return comment;
    }
}
