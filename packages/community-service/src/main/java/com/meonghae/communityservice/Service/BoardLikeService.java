package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardLike;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Repository.BoardLikeRepository;
import com.meonghae.communityservice.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.meonghae.communityservice.Exception.Error.ErrorCode.BAD_REQUEST;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final UserServiceClient userService;

    @Transactional
    public String addLike(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        String email = userService.getUserEmail(token);
        BoardLike like = likeRepository.findByEmailAndBoard(email, board);
        // 좋아요
        if(like == null) {
            BoardLike newLike = new BoardLike(email, board);
            board.setLikes(board.getLikes() + 1);
            likeRepository.save(newLike);
            return "추천 완료";
        } else if (like.getStatus()) {
            like.cancelLike(board);
            return "추천 취소";
        }
        else like.addLike(board);
        return "추천 완료";
    }
}
