package com.meonghae.communityservice.unit.domain.board;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.dto.board.BoardRequest;
import org.junit.jupiter.api.Test;

import static com.meonghae.communityservice.domain.board.BoardType.*;
import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    @Test
    public void Board_객체_생성_테스트() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        //when
        Board board = Board.create(request, SHOW, "abcabc@naver.com");

        //then
        assertThat(board.getTitle()).isEqualTo("test1");
        assertThat(board.getContent()).isEqualTo("testContent");
        assertThat(board.getType()).isEqualTo(SHOW);
        assertThat(board.getEmail()).isEqualTo("abcabc@naver.com");
        assertThat(board.getLikes()).isEqualTo(0);
        assertThat(board.getHasImage()).isFalse();
        assertThat(board.getCreatedDate()).isNull();
        assertThat(board.getModifiedDate()).isNull();
    }

    @Test
    public void Board_의_제목과_내용을_수정할_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board = Board.create(request, SHOW, "abcabc@naver.com");

        //when
        Board updateBoard = board.updateBoard("test2", "testContent-2");

        //then
        assertThat(updateBoard)
                .extracting("title", "content")
                .containsExactly("test2", "testContent-2");
    }

    @Test
    public void Board_의_이미지_존재_여부를_변경할_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board1 = Board.create(request, SHOW, "abcabc@naver.com");
        Board board2 = Board.create(request, SHOW, "abcabc@naver.com");

        //when
        board1.toggleHasImage();

        //then
        assertThat(board1.getHasImage()).isTrue();
        assertThat(board2.getHasImage()).isFalse();
    }
}