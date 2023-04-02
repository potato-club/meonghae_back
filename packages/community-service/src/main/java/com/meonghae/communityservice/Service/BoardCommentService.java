package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Dto.CommentRequestDto;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardComment;
import com.meonghae.communityservice.Repository.BoardCommentRepository;
import com.meonghae.communityservice.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository commentRepository;

    @Transactional
    public String addComment(Long boardId, CommentRequestDto requestDto) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(()->new RuntimeException("board is not exist"));
        BoardComment comment;
        String result;

        if(requestDto.getParentId() != null) {
            BoardComment parent = commentRepository.findById(requestDto.getParentId())
                    .orElse(null);
            if(parent != null && parent.isParent()) {
                comment = createComment(findBoard, requestDto);
                parent.addReply(comment);
                result = "자식댓글 저장 완료";
            } else {
                comment = createComment(findBoard, requestDto);
                result = "부모댓글 저장 완료";
            }
        } else {
            comment = createComment(findBoard, requestDto);
            result = "부모댓글 저장 완료";
        }
        commentRepository.save(comment);
        return result;
    }

    private BoardComment createComment(Board findBoard, CommentRequestDto requestDto) {
        BoardComment comment = BoardComment.builder()
                .board(findBoard)
                .comment(requestDto.getComment())
                .userId(requestDto.getUserId()).build();
        return comment;
    }
}
