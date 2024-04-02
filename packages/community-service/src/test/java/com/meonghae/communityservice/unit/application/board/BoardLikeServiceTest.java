package com.meonghae.communityservice.unit.application.board;

import com.meonghae.communityservice.application.board.BoardLikeService;
import com.meonghae.communityservice.application.board.BoardService;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.dto.board.BoardRequest;
import com.meonghae.communityservice.exception.custom.BoardException;
import com.meonghae.communityservice.exception.error.ErrorCode;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BoardLikeServiceTest {
    private BoardService boardService;
    private BoardLikeService likeService;
    
    @BeforeEach
    void init() {
        FakeUserService fakeUserService = new FakeUserService();
        FakeBoardRepo fakeBoardRepo = new FakeBoardRepo();
        FakeBoardLikeRepo fakeLikeRepo = new FakeBoardLikeRepo();

        this.boardService = BoardService.builder()
                .userService(fakeUserService)
                .s3Service(new FakeS3Service())
                .boardRepository(fakeBoardRepo)
                .likeRepository(fakeLikeRepo)
                .redisService(new FakeRedis())
                .build();

        this.likeService = BoardLikeService.builder()
                .boardRepository(fakeBoardRepo)
                .likeRepository(fakeLikeRepo)
                .userService(fakeUserService)
                .build();
    }

    @Test
    public void 게시글에_좋아요를_추가할_수_있다() throws Exception {
        //given
        Board board = createBoard();
        String token2 = "test token 2";

        //when
        String res = likeService.addLike(board.getId(), token2);

        //then
        assertThat(res).isEqualTo("추천 완료");
        assertThat(board.getLikes()).isEqualTo(1);
    } 
    
    @Test
    public void 존재하지_않는_게시글에_좋아요_시_예외가_던져진다() throws Exception {
        //given
        String token = "test token 3";

        //when //then
        assertThatThrownBy(() -> likeService.addLike(1L, token))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorMessage", "board is not exist");
    }

    @Test
    public void 좋아요_상태에서_재요청_시_좋아요가_취소된다() throws Exception {
        //given
        Board board = createBoard();
        String token = "test token 2";
        String res = likeService.addLike(board.getId(), token);

        assertThat(res).isEqualTo("추천 완료");
        assertThat(board.getLikes()).isEqualTo(1);

        //when
        String res2 = likeService.addLike(board.getId(), token);

        //then
        assertThat(res2).isEqualTo("추천 취소");
        assertThat(board.getLikes()).isZero();
    }

    @Test
    public void 좋아요를_취소했던_상태에서_다시_누를_경우_좋아요가_반영된다() throws Exception {
        //given
        Board board = createBoard();
        String token = "test token 2";
        likeService.addLike(board.getId(), token);
        String res = likeService.addLike(board.getId(), token);

        assertThat(res).isEqualTo("추천 취소");
        assertThat(board.getLikes()).isZero();

        //when
        String res2 = likeService.addLike(board.getId(), token);

        //then
        assertThat(res2).isEqualTo("추천 완료");
        assertThat(board.getLikes()).isEqualTo(1);
    }
    
    public Board createBoard() {
        int typeKey = 1;
        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";

        //when
        return boardService.createBoard(typeKey, request, token);
    }
}