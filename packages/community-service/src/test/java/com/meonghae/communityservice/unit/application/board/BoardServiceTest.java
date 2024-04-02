package com.meonghae.communityservice.unit.application.board;

import com.meonghae.communityservice.application.board.BoardService;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardType;
import com.meonghae.communityservice.dto.board.BoardRequest;
import com.meonghae.communityservice.dto.board.BoardUpdate;
import com.meonghae.communityservice.exception.custom.BoardException;
import com.meonghae.communityservice.exception.error.ErrorCode;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

// 단순한 로직만을 테스트하는 단위 테스트
class BoardServiceTest {

    private BoardService boardService;

    private final MockMultipartFile multipartFile = new MockMultipartFile(
            "testFile",
            "originTestFile",
            "testType",
            "content".getBytes()
    );

    @BeforeEach
    void init() {
        this.boardService = BoardService.builder()
                .userService(new FakeUserService())
                .s3Service(new FakeS3Service())
                .boardRepository(new FakeBoardRepo())
                .likeRepository(new FakeBoardLikeRepo())
                .redisService(new FakeRedis())
                .build();
    }

    @Test
    public void 게시글을_생성할_수_있다() throws Exception {
        //given
        int typeKey = 1;
        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";
        
        //when
        Board board = boardService.createBoard(typeKey, request, token);

        //then
        assertThat(board.getId()).isEqualTo(1L);
        assertThat(board.getTitle()).isEqualTo("test title");
        assertThat(board.getContent()).isEqualTo("test content");
        assertThat(board.getType()).isEqualByComparingTo(BoardType.SHOW);
        assertThat(board.getEmail()).isEqualTo(token + "@test.com");
        assertThat(board.getHasImage()).isFalse();
    }

    @Test
    public void 이미지가_있는_게시글을_생성할_수_있다() throws Exception {
        //given
        int typeKey = 1;
        List<MultipartFile> images = new ArrayList<>();
        images.add(multipartFile);

        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .images(images)
                .build();
        String token = "test token";

        //when
        Board board = boardService.createBoard(typeKey, request, token);

        //then
        assertThat(board.getTitle()).isEqualTo("test title");
        assertThat(board.getContent()).isEqualTo("test content");
        assertThat(board.getType()).isEqualByComparingTo(BoardType.SHOW);
        assertThat(board.getEmail()).isEqualTo(token + "@test.com");
        assertThat(board.getHasImage()).isTrue();
    }

    @Test
    public void 게시글을_수정할_수_있다() throws Exception {
        //given
        int typeKey = 1;

        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";

        Board board = boardService.createBoard(typeKey, request, token);

        BoardUpdate update = BoardUpdate.builder()
                .title("new title")
                .content("new content")
                .build();

        //when
        Board modifyBoard = boardService.modifyBoard(board.getId(), update, token);

        //then
        assertThat(modifyBoard.getTitle()).isEqualTo("new title");
        assertThat(modifyBoard.getContent()).isEqualTo("new content");
        assertThat(modifyBoard.getType()).isEqualByComparingTo(BoardType.SHOW);
        assertThat(modifyBoard.getEmail()).isEqualTo(token + "@test.com");
        assertThat(modifyBoard.getHasImage()).isFalse();
    }

    @Test
    public void 작성자가_아닐_경우_게시글_수정_시_예외가_던져진다() throws Exception {
        //given
        int typeKey = 1;

        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";
        String token_2 = "unknown token";

        Board board = boardService.createBoard(typeKey, request, token);

        BoardUpdate update = BoardUpdate.builder()
                .title("new title")
                .content("new content")
                .build();

        //when
        //then
        assertThatThrownBy(() -> boardService.modifyBoard(board.getId(), update, token_2))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "글 작성자만 수정 가능합니다.");
    }

    @Test
    public void 게시글을_삭제할_수_있다() throws Exception {
        //given
        int typeKey = 1;

        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";


        //when
        Long id = boardService.createBoard(typeKey, request, token).getId();
        boardService.deleteBoard(id, token);

        //then
        assertThatThrownBy(() -> boardService.getBoard(id, token))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "board is not exist");
    }

    @Test
    public void 작성자가_아닐_경우_게시글_삭제_시_예외가_던져진다() throws Exception {
        //given
        int typeKey = 1;

        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";
        String token_2 = "unknown token";

        //when
        Long id = boardService.createBoard(typeKey, request, token).getId();

        //then
        assertThatThrownBy(() -> boardService.deleteBoard(id, token_2))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "글 작성자만 삭제 가능합니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    @Test
    public void 이메일로_유저의_게시글을_전부_삭제할_수_있다() throws Exception {
        //given
        int typeKey = 1;

        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";

        String email = token + "@test.com";

        //when
        Long id = boardService.createBoard(typeKey, request, token).getId();
        boardService.deleteBoardByEmail(email);

        //then
        assertThatThrownBy(() -> boardService.getBoard(id, token))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "board is not exist");
    }
}