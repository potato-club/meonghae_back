package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardLike;
import com.meonghae.communityservice.Repository.BoardLikeRepository;
import com.meonghae.communityservice.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeRepository likeRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public String addLike(Long id, String userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("board is not exist"));
        BoardLike like = likeRepository.findByUserId(userId);
        // 좋아요
        if(like == null) {
            BoardLike newLike = new BoardLike(userId, board);
            board.setLikes(board.getLikes() + 1);
            likeRepository.save(newLike);
            return "추천 완료";
        } else if (like.getStatus() == true) {
            like.cancelLike(board);
            return "추천 취소";
        }
        else like.addLike(board);
        return "추천 완료";
    }
}
