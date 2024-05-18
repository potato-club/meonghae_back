package com.meonghae.communityservice.unit.domain.board;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.dto.board.BoardRequest;
import org.junit.jupiter.api.Test;

import static com.meonghae.communityservice.domain.board.BoardType.SHOW;
import static org.assertj.core.api.Assertions.assertThat;

class BoardCommentTest {

    @Test
    public void 부모_댓글을_생성할_수_있다() throws Exception {
        //given
        BoardRequest boardRequest = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board = Board.create(boardRequest, SHOW, "abcabc@naver.com");

        String comment = "test comment";
        String email = "test@naver.com";

        //when
        BoardComment boardComment = BoardComment.create(board, comment, email);

        //then
        assertThat(boardComment.getParent()).isNull();
        assertThat(boardComment)
                .extracting("board", "comment", "updated", "email")
                .containsExactly(board, comment, false, email);
    } 

    @Test
    public void 부모_댓글을_생성하고_자식_댓글을_달고_부모_댓글에_연결할_수_있다() throws Exception {
        //given
        BoardRequest boardRequest = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board = Board.create(boardRequest, SHOW, "abcabc@naver.com");

        String parentComment = "parent comment";
        String childComment = "child comment";
        String email = "test@naver.com";

        //when
        BoardComment parent = BoardComment.create(board, parentComment, email);
        BoardComment child = BoardComment.create(board, childComment, email);

        child.setParent(parent);
        parent.addReply(child);

        //then
        assertThat(parent.getReplies()).hasSize(1).contains(child);
        assertThat(parent.isParent()).isTrue();
        assertThat(child.getParent()).isEqualTo(parent);
    }

    @Test
    public void 댓글을_수정할_수_있다() throws Exception {
        //given
        BoardRequest boardRequest = BoardRequest.builder()
                .title("test1")
                .content("testContent")
                .build();

        Board board = Board.create(boardRequest, SHOW, "abcabc@naver.com");

        String content = "parent comment";
        String email = "test@naver.com";

        BoardComment comment = BoardComment.create(board, content, email);

        assertThat(comment.getComment()).isEqualTo(content);

        //when
        BoardComment updateComment = comment.updateComment("change comment");

        //then
        assertThat(updateComment.getComment()).isEqualTo("change comment");
        assertThat(updateComment.getUpdated()).isTrue();
    }
}