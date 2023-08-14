package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.UserServiceClient;
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
import java.util.Objects;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository commentRepository;
    private final RedisService redisService;
    private final UserServiceClient userService;

    @Transactional
    public Slice<CommentParentDto> getParentComments(int page, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        PageRequest request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "id"));
        Slice<BoardComment> comments = commentRepository.findByBoardAndParentIsNull(request, board);
        Slice<CommentParentDto> dtoPage = comments.map(comment -> {
            String url = redisService.getProfileImage(comment.getEmail());
            return Objects.equals(comment.getEmail(), board.getEmail()) ?
                    new CommentParentDto(comment, url, true) : new CommentParentDto(comment, url, false);
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
            String url = redisService.getProfileImage(comment.getEmail());
            return Objects.equals(comment.getBoard().getEmail(), comment.getEmail()) ?
                    new CommentChildDto(comment, url, true) : new CommentChildDto(comment, url, false);
        });
        return dtoPage;
    }

    @Transactional
    public void addParentComment(Long boardId, CommentRequestDto requestDto, String token) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        BoardComment parent = createComment(findBoard, requestDto, token);
        commentRepository.save(parent);
    }

    @Transactional
    public void addChildComment(Long parentId, CommentRequestDto requestDto, String token) {
        BoardComment parent = commentRepository.findById(parentId)
                .orElse(null);
        if(parent == null || !parent.isParent()) {
            throw new CommentException(ErrorCode.BAD_REQUEST, "해당 댓글에는 대댓글을 달 수 없습니다.");
        }
        BoardComment child = createComment(parent.getBoard(), requestDto, token);
        commentRepository.save(child);
        parent.addReply(child);
    }

    @Transactional
    public String updateComment(Long id, CommentRequestDto updateDto, String token) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment is not exist"));
        if(!comment.getEmail().equals(userService.getUserEmail(token))) {
            throw new CommentException(ErrorCode.UNAUTHORIZED, "댓글 작성자만 수정 가능합니다.");
        }
        comment.updateComment(updateDto.getComment());
        if(comment.isParent()) {
            return "부모댓글 수정 완료";
        }
        return "자식댓글 수정 완료";
    }

    @Transactional
    public String deleteComment(Long id, String token) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment is not exist"));
        if(!comment.getEmail().equals(userService.getUserEmail(token))) {
            throw new CommentException(ErrorCode.UNAUTHORIZED, "댓글 작성자만 삭제 가능합니다.");
        }
        commentRepository.delete(comment);
        if(comment.isParent()) {
            return "부모댓글 삭제 완료";
        }
        return "자식댓글 삭제 완료";
    }

    private BoardComment createComment(Board findBoard, CommentRequestDto requestDto, String token) {
        String email = userService.getUserEmail(token);
        BoardComment comment = BoardComment.builder()
                .board(findBoard)
                .comment(requestDto.getComment())
                .updated(false)
                .email(email).build();
        return comment;
    }
}
