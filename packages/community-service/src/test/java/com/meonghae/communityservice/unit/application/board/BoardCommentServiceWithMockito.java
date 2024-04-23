package com.meonghae.communityservice.unit.application.board;

import com.meonghae.communityservice.application.board.BoardCommentService;
import com.meonghae.communityservice.application.board.port.BoardRepository;
import com.meonghae.communityservice.application.board.port.CommentRepository;
import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.domain.board.BoardType;
import com.meonghae.communityservice.dto.board.BoardRequest;
import com.meonghae.communityservice.dto.comment.CommentChildDto;
import com.meonghae.communityservice.dto.comment.CommentParentDto;
import com.meonghae.communityservice.exception.custom.CommentException;
import com.meonghae.communityservice.exception.error.ErrorCode;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BoardCommentServiceWithMockito {

    private BoardCommentService commentService;

    private CommentRepository commentRepository = mock(FakeCommentRepo.class);

    private BoardRepository boardRepository = mock(FakeBoardRepo.class);

    private RedisPort redisPort = mock(FakeRedis.class);

    private UserServicePort userService = mock(FakeUserService.class);

    @BeforeEach
    void init() {
        this.commentService = BoardCommentService.builder()
                .boardRepository(boardRepository)
                .commentRepository(commentRepository)
                .redisService(redisPort)
                .userService(userService)
                .build();
    }

    @Test
    public void 부모_댓글_리스트를_가져올_수_있다() throws Exception {
        //given
        Board board = createBoard();

        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));

        BoardComment comment1 = BoardComment.create(board, "test1", "tester@test.com");
        BoardComment comment2 = BoardComment.create(board, "test2", "tester@test.com");

        Slice<BoardComment> mockSlice = new PageImpl<>(List.of(comment1, comment2));
        when(commentRepository.findByBoard_IdAndParentIsNull(any(PageRequest.class), anyLong()))
                .thenReturn(mockSlice);

        when(redisPort.getProfileImage(anyString()))
                .thenReturn("testImage.jpg");

        //when
        Slice<CommentParentDto> parentComments = commentService.getParentComments(1, 1L);

        //then
        assertThat(parentComments.getNumberOfElements()).isEqualTo(2);
        assertThat(parentComments.getContent()).hasSize(2);
        assertThat(parentComments.getContent().get(0).getComment()).isEqualTo("test1");
        assertThat(parentComments.getContent().get(0).getReplies()).isZero();
        assertThat(parentComments.getContent().get(1).getComment()).isEqualTo("test2");
        assertThat(parentComments.getContent().get(1).getReplies()).isZero();
    }

    @Test
    public void 자식_댓글_리스트를_가져올_수_있다() throws Exception {
        //given
        Board board = createBoard();
        BoardComment parentComment = BoardComment.builder()
                .id(1L)
                .board(board)
                .comment("test1")
                .updated(false)
                .email("tester@test.com").build();

        BoardComment child1 = BoardComment.create(board, "child1", "child@test.com");
        child1.setParent(parentComment);
        BoardComment child2 = BoardComment.create(board, "child2", "child@test.com");
        child2.setParent(parentComment);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(parentComment));

        Slice<BoardComment> childComments = new PageImpl<>(List.of(child1, child2));

        when(commentRepository.findByParent_Id(any(PageRequest.class), anyLong()))
                .thenReturn(childComments);

        when(redisPort.getProfileImage(anyString()))
                .thenReturn("child.jpg");

        //when
        Slice<CommentChildDto> childSlice = commentService.getChildComments(1, 1L);

        //then
        assertThat(childSlice.getNumberOfElements()).isEqualTo(2);
        assertThat(childSlice.getContent()).hasSize(2);
        assertThat(childSlice.getContent().get(0).getComment()).isEqualTo("child1");
        assertThat(childSlice.getContent().get(1).getComment()).isEqualTo("child2");
    }

    @Test
    public void 부모_댓글이_없을_때_자식_댓글_호출_시_예외가_던져진다() throws Exception {
        //given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> commentService.getChildComments(1, 1L))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorMessage", "this comment is not parent");
    }

    private static Board createBoard() {
        BoardRequest request = BoardRequest.builder()
                .title("test title")
                .content("test content")
                .build();
        String token = "test token";

        return Board.create(request, BoardType.SHOW, token);
    }
}
