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
    public String addLike(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        String email = userService.getUserEmail(token);
        BoardLike like = likeRepository.findByEmailAndBoardEntity_Id(email, id);
        // 좋아요
        if (like != null && like.getStatus()) {
            like.cancelLike();
            boardRepository.save(board);
            likeRepository.save(like);
            return "추천 취소";
        }

        if (like != null) {
            like.addLike();
        }

        if (like == null) {
            like = BoardLike.create(email, board);
        }

        boardRepository.save(board);
        likeRepository.save(like);
        return "추천 완료";
    }
}
