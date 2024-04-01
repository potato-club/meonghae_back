package com.meonghae.communityservice.unit.application.board;

import com.meonghae.communityservice.application.board.BoardCommentService;
import com.meonghae.communityservice.application.board.BoardService;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.dto.board.BoardRequest;
import com.meonghae.communityservice.dto.comment.CommentRequest;
import com.meonghae.communityservice.exception.custom.CommentException;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BoardCommentServiceTest {
    private BoardCommentService commentService;
    private BoardService boardService;

    @BeforeEach
    void init() {
        FakeBoardRepo fakeBoardRepo = new FakeBoardRepo();
        FakeRedis fakeRedis = new FakeRedis();
        FakeUserService fakeUserService = new FakeUserService();

        this.commentService = BoardCommentService.builder()
                .boardRepository(fakeBoardRepo)
                .commentRepository(new FakeCommentRepo())
                .redisService(fakeRedis)
                .userService(fakeUserService)
                .build();

        this.boardService = BoardService.builder()
                .userService(fakeUserService)
                .s3Service(new FakeS3Service())
                .boardRepository(fakeBoardRepo)
                .likeRepository(new FakeBoardLikeRepo())
                .redisService(fakeRedis)
                .build();
    }

    @Test
    public void 게시글에_부모_댓글을_추가할_수_있다() throws Exception {
        //given
        Board board = createBoard();
        CommentRequest request = CommentRequest.builder()
                .comment("test parent comment")
                .build();
        String token = "tester";

        //when
        BoardComment comment = commentService.addParentComment(board.getId(), request, token);

        //then
        assertThat(comment.getComment()).isEqualTo(request.getComment());
        assertThat(comment.getBoard()).isEqualTo(board);
        assertThat(comment.getEmail()).isEqualTo(token + "@test.com");
        assertThat(board.getComments()).hasSize(1);
    }

    @Test
    public void 부모_댓글에_자식_댓글을_추가할_수_있다() throws Exception {
        //given
        Board board = createBoard();

        CommentRequest req1 = CommentRequest.builder()
                .comment("test parent comment")
                .build();

        CommentRequest req2 = CommentRequest.builder()
                .comment("test child comment")
                .build();
        String token_1 = "parent";
        String token_2 = "child";

        BoardComment parentComment = commentService.addParentComment(board.getId(), req1, token_1);

        //when
        BoardComment childComment = commentService.addChildComment(parentComment.getId(), req2, token_2);

        //then
        assertThat(childComment.getParent()).isEqualTo(parentComment);
        assertThat(childComment.getBoard()).isEqualTo(board);
        assertThat(parentComment.getReplies()).hasSize(1);
        assertThat(board.getComments()).hasSize(2);
        assertThat(board.getComments().get(0).getId()).isNotNull();
        assertThat(board.getComments().get(1).getId()).isNotNull();
    }

    @Test
    public void 자식_댓글에는_대댓글을_추가할_수_없다() throws Exception {
        //given
        Board board = createBoard();

        CommentRequest req1 = CommentRequest.builder()
                .comment("test parent comment")
                .build();

        CommentRequest req2 = CommentRequest.builder()
                .comment("test child comment")
                .build();

        CommentRequest req3 = CommentRequest.builder()
                .comment("test comment")
                .build();

        String token_1 = "parent";
        String token_2 = "child";

        BoardComment parentComment = commentService.addParentComment(board.getId(), req1, token_1);
        BoardComment childComment = commentService.addChildComment(parentComment.getId(), req2, token_2);

        //when
        //then
        assertThatThrownBy(() -> commentService.addChildComment(childComment.getId(), req3, token_1))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "해당 댓글에는 대댓글을 달 수 없습니다.");
    }

    @Test
    public void 댓글을_업데이트_할_수_있다() throws Exception {
        //given
        Board board = createBoard();
        String token = "tester";

        CommentRequest req = CommentRequest.builder()
                .comment("test comment")
                .build();

        BoardComment comment = commentService.addParentComment(board.getId(), req, token);

        assertThat(comment.getComment()).isEqualTo("test comment");

        //when
        CommentRequest update = CommentRequest.builder()
                .comment("update comment")
                .build();

        BoardComment updateComment = commentService.updateComment(comment.getId(), update, token);

        //then
        assertThat(updateComment.getUpdated()).isTrue();
        assertThat(updateComment.getComment()).isEqualTo(update.getComment());
        assertThat(board.getComments()).hasSize(1);
    }

    @Test
    public void 댓글_작성자가_아니면_댓글_수정_시_예외가_던져진다() throws Exception {
        //given
        Board board = createBoard();
        String token = "tester";

        CommentRequest req = CommentRequest.builder()
                .comment("test comment")
                .build();

        BoardComment comment = commentService.addParentComment(board.getId(), req, token);

        assertThat(comment.getComment()).isEqualTo("test comment");

        //when
        //then
        String token2 = "tester-2";
        CommentRequest update = CommentRequest.builder()
                .comment("update comment")
                .build();

        assertThatThrownBy(() -> commentService.updateComment(comment.getId(), update, token2))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "댓글 작성자만 수정 가능합니다.");
    }

    @Test
    public void 댓글_작성자가_아니면_댓글_삭제_시_예외가_던져진다() throws Exception {
        //given
        Board board = createBoard();
        String token = "tester";

        CommentRequest req = CommentRequest.builder()
                .comment("test comment")
                .build();

        BoardComment comment = commentService.addParentComment(board.getId(), req, token);

        assertThat(comment.getComment()).isEqualTo("test comment");

        //when
        //then
        String token2 = "tester-2";

        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), token2))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "댓글 작성자만 삭제 가능합니다.");
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