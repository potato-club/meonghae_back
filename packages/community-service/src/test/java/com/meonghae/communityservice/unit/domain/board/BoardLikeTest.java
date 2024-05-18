package com.meonghae.communityservice.unit.domain.board;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardLike;
import com.meonghae.communityservice.dto.board.BoardRequest;
import org.junit.jupiter.api.Test;

import static com.meonghae.communityservice.domain.board.BoardType.SHOW;
import static org.assertj.core.api.Assertions.assertThat;

class BoardLikeTest {

    @Test
    public void 게시글_좋아요를_할_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board = Board.create(request, SHOW, "board@naver.com");

        //when
        BoardLike like = BoardLike.create("likeUser@naver.com", board);

        //then
        assertThat(like.getBoard()).isEqualTo(board);
        assertThat(like.getEmail()).isEqualTo("likeUser@naver.com");
        assertThat(like.getStatus()).isTrue();
    }

    @Test
    public void 게시글_좋아요를_취소할_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board = Board.create(request, SHOW, "board@naver.com");
        BoardLike like = BoardLike.create("likeUser@naver.com", board);

        //when
        like.toggleLike();

        //then
        assertThat(like.getBoard()).isEqualTo(board);
        assertThat(like.getEmail()).isEqualTo("likeUser@naver.com");
        assertThat(like.getStatus()).isFalse();
    }

    @Test
    public void 취소했던_좋아요를_다시_누를_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board = Board.create(request, SHOW, "board@naver.com");
        BoardLike like = BoardLike.create("likeUser@naver.com", board);

        like.toggleLike();

        //when
        like.toggleLike();

        //then
        assertThat(like.getBoard()).isEqualTo(board);
        assertThat(like.getEmail()).isEqualTo("likeUser@naver.com");
        assertThat(like.getStatus()).isTrue();
    }
}