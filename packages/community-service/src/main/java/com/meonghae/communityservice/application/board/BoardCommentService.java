package com.meonghae.communityservice.application.board;

import com.meonghae.communityservice.application.FcmService;
import com.meonghae.communityservice.application.board.port.BoardRepository;
import com.meonghae.communityservice.application.board.port.CommentRepository;
import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.dto.comment.*;
import com.meonghae.communityservice.exception.custom.BoardException;
import com.meonghae.communityservice.exception.custom.CommentException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.meonghae.communityservice.exception.error.ErrorCode.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@Builder
@RequiredArgsConstructor
public class BoardCommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final RedisPort redisService;
    private final UserServicePort userService;
//    private final FcmService fcmService;

    public Slice<CommentParent> getParentComments(int page, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        PageRequest request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "id"));
        Slice<BoardComment> comments = commentRepository.findByBoard_IdAndParentIsNull(request, boardId);
        Slice<CommentParent> dtoPage = comments.map(comment -> {
            String url = redisService.getProfileImage(comment.getEmail());
            return Objects.equals(comment.getEmail(), board.getEmail()) ?
                    new CommentParent(comment, url, true) : new CommentParent(comment, url, false);
        });
        return dtoPage;
    }

    public Slice<CommentChild> getChildComments(int page, Long parentId) {
        BoardComment parent = commentRepository.findById(parentId).orElse(null);
        if (parent == null || !parent.isParent()) {
            throw new CommentException(BAD_REQUEST, "this comment is not parent");
        }
        PageRequest request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.ASC, "id"));
        Slice<BoardComment> childComments = commentRepository.findByParent_Id(request, parent.getId());
        Slice<CommentChild> dtoPage = childComments.map(comment -> {
            String url = redisService.getProfileImage(comment.getEmail());
            return Objects.equals(comment.getBoard().getEmail(), comment.getEmail()) ?
                    new CommentChild(comment, url, true) : new CommentChild(comment, url, false);
        });
        return dtoPage;
    }

    @Transactional
    public BoardComment addParentComment(Long boardId, CommentRequest requestDto, String token) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));

        BoardComment parent = createComment(findBoard, requestDto, token);
//        fcmService.pushMessage(findBoard, requestDto);
        BoardComment comment = commentRepository.save(parent);
        findBoard.addComment(comment);
        boardRepository.save(findBoard);

        return comment;
    }

    @Transactional
    public BoardComment addChildComment(Long parentId, CommentRequest requestDto, String token) {
        BoardComment parent = commentRepository.findById(parentId)
                .orElse(null);

        if (parent == null || !parent.isParent()) {
            throw new CommentException(BAD_REQUEST, "해당 댓글에는 대댓글을 달 수 없습니다.");
        }

        // 순서
        // 1. 자식댓글에 부모 / 게시글 설정해서 저장 => 메소드 분리
        // 2. 이후 반환값으로 부모와 게시글에 각각 댓글 저장
        BoardComment child = createComment(parent.getBoard(), requestDto, token);
        child.setParent(parent);
        BoardComment saveChild = commentRepository.save(child);

        parent.addReply(saveChild);
        commentRepository.save(parent);

        Board board = parent.getBoard();
        boardRepository.save(board);

        return child;
    }

    @Transactional
    public BoardComment updateComment(Long id, CommentRequest updateDto, String token) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException(NOT_FOUND, "comment is not exist"));
        if (!comment.getEmail().equals(userService.getUserEmail(token))) {
            throw new CommentException(UNAUTHORIZED, "댓글 작성자만 수정 가능합니다.");
        }

        BoardComment updateComment = comment.updateComment(updateDto.getComment());
        return commentRepository.save(updateComment);
    }

    @Transactional
    public void deleteComment(Long id, String token) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException(NOT_FOUND, "comment is not exist"));
        if (!comment.getEmail().equals(userService.getUserEmail(token))) {
            throw new CommentException(UNAUTHORIZED, "댓글 작성자만 삭제 가능합니다.");
        }
        commentRepository.delete(comment);
    }

    private BoardComment createComment(Board findBoard, CommentRequest requestDto, String token) {
        String email = userService.getUserEmail(token);
        return BoardComment.create(findBoard, requestDto.getComment(), email);
    }
}
