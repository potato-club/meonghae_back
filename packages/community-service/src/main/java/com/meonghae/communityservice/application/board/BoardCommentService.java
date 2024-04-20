package com.meonghae.communityservice.application.board;

import com.meonghae.communityservice.application.board.port.BoardRepository;
import com.meonghae.communityservice.application.board.port.CommentRepository;
import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.dto.comment.CommentChildDto;
import com.meonghae.communityservice.dto.comment.CommentParentDto;
import com.meonghae.communityservice.dto.comment.CommentRequest;
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

    public Slice<CommentParentDto> getParentComments(int page, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        PageRequest request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "id"));
        Slice<BoardComment> comments = commentRepository.findByBoard_IdAndParentIsNull(request, boardId);

        return comments.map(comment -> {
            String url = redisService.getProfileImage(comment.getEmail());
            return Objects.equals(comment.getEmail(), board.getEmail()) ?
                    new CommentParentDto(comment, url, true) : new CommentParentDto(comment, url, false);
        });
    }

    public Slice<CommentChildDto> getChildComments(int page, Long parentId) {
        BoardComment parent = commentRepository.findById(parentId).orElse(null);
        if (parent == null || !parent.isParent()) {
            throw new CommentException(BAD_REQUEST, "this comment is not parent");
        }
        PageRequest request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.ASC, "id"));
        Slice<BoardComment> childComments = commentRepository.findByParent_Id(request, parent.getId());

        return childComments.map(comment -> {
            String url = redisService.getProfileImage(comment.getEmail());
            return Objects.equals(comment.getBoard().getEmail(), comment.getEmail()) ?
                    new CommentChildDto(comment, parentId, url, true) :
                    new CommentChildDto(comment, parentId, url, false);
        });
    }

    @Transactional
    public BoardComment addParentComment(Long boardId, CommentRequest requestDto, String token) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));

        BoardComment parent = createComment(findBoard, requestDto, token);
//        fcmService.pushMessage(findBoard, requestDto);

        return commentRepository.save(parent);
    }

    @Transactional
    public BoardComment addChildComment(Long parentId, CommentRequest requestDto, String token) {
        BoardComment parent = commentRepository.findById(parentId)
                .orElse(null);

        if (parent == null || !parent.isParent()) {
            throw new CommentException(BAD_REQUEST, "해당 댓글에는 대댓글을 달 수 없습니다.");
        }

        BoardComment child = createComment(parent.getBoard(), requestDto, token);

        return commentRepository.saveChild(parent, child);
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
        commentRepository.delete(id);
    }

    private BoardComment createComment(Board findBoard, CommentRequest requestDto, String token) {
        String email = userService.getUserEmail(token);
        return BoardComment.create(findBoard, requestDto.getComment(), email);
    }
}
