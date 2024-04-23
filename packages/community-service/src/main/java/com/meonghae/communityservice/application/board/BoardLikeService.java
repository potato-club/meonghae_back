package com.meonghae.communityservice.application.board;

import com.meonghae.communityservice.application.board.port.BoardLikeRepository;
import com.meonghae.communityservice.application.board.port.BoardRepository;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardLike;
import com.meonghae.communityservice.exception.custom.BoardException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.meonghae.communityservice.exception.error.ErrorCode.BAD_REQUEST;

@Service
@Slf4j
@Transactional(readOnly = true)
@Builder
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final UserServicePort userService;

    @Transactional
    public String toggleLike(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        String email = userService.getUserEmail(token);
        BoardLike like = likeRepository.findByEmailAndBoardEntity_Id(email, id);

        // 좋아요
        if (like != null) {
            if (like.getStatus()) {
                cancelLike(like, board);
                return "추천 취소";
            } else {
                addLike(like, board);
                return "추천 완료";
            }
        } else {
            like = BoardLike.create(email, board);
            likeRepository.save(like);
            board.incrementLikes();
            boardRepository.save(board);
            return "추천 완료";
        }
    }

    private void addLike(BoardLike like, Board board) {
        like.toggleLike();
        likeRepository.save(like);

        board.incrementLikes();
        boardRepository.save(board);
    }

    private void cancelLike(BoardLike like, Board board) {
        like.toggleLike();
        likeRepository.save(like);

        board.decrementLikes();
        boardRepository.save(board);
    }
}
