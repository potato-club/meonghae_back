package com.meonghae.communityservice.web.board.comment;

import com.meonghae.communityservice.dto.comment.CommentChild;
import com.meonghae.communityservice.dto.comment.CommentParent;
import com.meonghae.communityservice.dto.comment.CommentRequest;
import com.meonghae.communityservice.application.board.BoardCommentService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boardComments")
@RequiredArgsConstructor
@Api(value = "COMMENT_CONTROLLER", tags = "게시판 댓글 서비스 컨트롤러")
public class BoardCommentController {
    private final BoardCommentService commentService;

    @Operation(summary = "부모 댓글 조회 API")
    @GetMapping("/{boardId}")
    public Slice<CommentParent> getParentComments(@PathVariable(name = "boardId") Long id,
                                                  @RequestParam(required = false,
                                                            defaultValue = "1", value = "p") int page) {
        return commentService.getParentComments(page, id);
    }

    @Operation(summary = "자식 댓글 조회 API")
    @GetMapping("/{parentId}/reply")
    public Slice<CommentChild> getChildComments(@PathVariable(name = "parentId") Long parentId,
                                                @RequestParam(required = false,
                                                          defaultValue = "1", value = "p") int page) {
        return commentService.getChildComments(page, parentId);
    }

    @Operation(summary = "부모 댓글 작성 API")
    @PostMapping("/{boardId}")
    public ResponseEntity<String> createComment(@PathVariable(name = "boardId") Long id,
                                                @RequestBody CommentRequest requestDto,
                                                @RequestHeader("Authorization") String token) {
        commentService.addParentComment(id, requestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 생성 완료");
    }

    @Operation(summary = "자식 댓글 작성 API")
    @PostMapping("/{parentId}/reply")
    public ResponseEntity<String> addChildComment(@PathVariable(name = "parentId") Long parentId,
                                                  @RequestBody CommentRequest requestDto,
                                                  @RequestHeader("Authorization") String token) {
        commentService.addChildComment(parentId, requestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body("대댓글 생성 완료");
    }

    @Operation(summary = "댓글 수정 API")
    @PutMapping("/{commentId}")
    public ResponseEntity<String> modifyingComment(@PathVariable(name = "commentId") Long id,
                                                   @RequestBody CommentRequest updateDto,
                                                   @RequestHeader("Authorization") String token) {
        commentService.updateComment(id, updateDto, token);
        return ResponseEntity.ok("댓글 수정 완료");
    }

    @Operation(summary = "댓글 삭제 API")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable(name = "commentId") Long id,
                                                @RequestHeader("Authorization") String token) {
        commentService.deleteComment(id, token);
        return ResponseEntity.ok("댓글 삭제 완료");
    }
}
