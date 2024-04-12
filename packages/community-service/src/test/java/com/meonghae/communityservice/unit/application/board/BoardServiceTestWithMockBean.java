package com.meonghae.communityservice.unit.application.board;

import com.meonghae.communityservice.application.board.BoardService;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardType;
import com.meonghae.communityservice.dto.board.BoardDetail;
import com.meonghae.communityservice.dto.board.BoardList;
import com.meonghae.communityservice.dto.board.BoardMain;
import com.meonghae.communityservice.dto.board.BoardRequest;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BoardServiceTestWithMockBean {

    private BoardService boardService;

    private FakeBoardRepo boardRepository = mock(FakeBoardRepo.class);

    private FakeRedis redisPort = mock(FakeRedis.class);

    private FakeS3Service s3Service = mock(FakeS3Service.class);

    private FakeUserService userService = mock(FakeUserService.class);

    private FakeBoardLikeRepo likeRepository = mock(FakeBoardLikeRepo.class);

    @BeforeEach
    void init() {
        this.boardService = BoardService.builder()
                .userService(userService)
                .s3Service(s3Service)
                .boardRepository(boardRepository)
                .likeRepository(likeRepository)
                .redisService(redisPort)
                .build();
    }

    @Test
    public void 게시글_리스트를_가져올_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";

        Board board = Board.create(request, BoardType.SHOW, token);
        board.toggleHasImage();
        Slice<Board> mockSlice = new PageImpl<>(Collections.singletonList(board));

        when(boardRepository.findByType(any(), any(PageRequest.class)))
                .thenReturn(mockSlice);

        when(redisPort.getProfileImage(anyString()))
                .thenReturn("testImage.jpg");

        //when
        Slice<BoardList> result = boardService.getBoardList(1, 1);

        //then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("test title");
        assertThat(result.getContent().get(0).getContent()).isEqualTo("test content");
        assertThat(result.getContent().get(0).getProfileUrl()).isEqualTo("testImage.jpg");
        assertThat(result.getContent().get(0).isHasImage()).isTrue();
    }

    @Test
    public void 특정_게시글_정보를_가져올_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";

        Board board = Board.create(request, BoardType.SHOW, token);
        board.toggleHasImage();

        when(userService.getUserEmail(anyString()))
                .thenReturn(token);

        when(boardRepository.findById(any()))
                .thenReturn(Optional.of(board));

        when(likeRepository.findByEmailAndBoardEntity_Id(anyString(), any()))
                .thenReturn(null);

        when(redisPort.getProfileImage(anyString()))
                .thenReturn("testImage.jpg");

        when(s3Service.getImages(any()))
                .thenReturn(new ArrayList<>());

        //when
        BoardDetail result = boardService.getBoard(1L, token);

        //then
        assertThat(result.getTitle()).isEqualTo("test title");
        assertThat(result.getContent()).isEqualTo("test content");
        assertThat(result.getLikes()).isZero();
        assertThat(result.getImages()).isEmpty();
        assertThat(result.getProfileUrl()).isEqualTo("testImage.jpg");
        assertThat(result.getCommentSize()).isZero();
    }

    @Test
    public void 메인페이지에_띄울_게시글을_가져올_수_있다() throws Exception {
        //given
        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";

        Board board1 = Board.create(request, BoardType.SHOW, token);
        Board board2 = Board.create(request, BoardType.FUN, token);
        Board board3 = Board.create(request, BoardType.MISSING, token);

        //when
        when(boardRepository.findBoardListForMain(any()))
                .thenReturn((List.of(board1, board2, board3)));

        //then
        List<BoardMain> mainBoard = boardService.getMainBoard();

        assertThat(mainBoard).hasSize(3);
    }
}
