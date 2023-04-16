package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.CommentDto.*;
import com.meonghae.communityservice.Dto.UserDto;
import com.meonghae.communityservice.Service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boardComments")
@RequiredArgsConstructor
public class BoardCommentController {
    private final BoardCommentService commentService;

    @GetMapping("/{boardId}")
    public Slice<CommentParentDto> getParentComments(@PathVariable(name = "boardId") Long id,
                                                     @RequestParam(required = false,
                                                            defaultValue = "1", value = "page") int page) {
        return commentService.getParentComments(page, id);
    }
    @GetMapping("/{parentId}/reply")
    public Slice<CommentChildDto> getChildComments(@PathVariable(name = "parentId") Long parentId,
                                                  @RequestParam(required = false,
                                                          defaultValue = "1", value = "page") int page) {
        return commentService.getChildComments(page, parentId);
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<Slice<CommentParentDto>> createComment(@PathVariable(name = "boardId") Long id,
                                                                @RequestBody CommentRequestDto requestDto) {
        commentService.addParentComment(id, requestDto);
        Slice<CommentParentDto> result = commentService.getParentComments(1, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @PostMapping("/{parentId}/reply")
    public ResponseEntity<Slice<CommentChildDto>> addChildComment(@PathVariable(name = "parentId") Long parentId,
                                  @RequestBody CommentRequestDto requestDto) {
        commentService.addChildComment(parentId, requestDto);
        Slice<CommentChildDto> result = commentService.getChildComments(1, parentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Slice<?>> modifyingComment(@PathVariable(name = "commentId") Long id,
                                                    @RequestBody CommentUpdateDto updateDto) {
        ReloadCommentDto reload = commentService.updateComment(id, updateDto);
        if(reload.getParent()) {
            return ResponseEntity.ok(commentService.getParentComments(1, reload.getId()));
        }
        return ResponseEntity.ok(commentService.getChildComments(1, reload.getId()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Slice<?>> deleteComment(@PathVariable(name = "commentId") Long id,
                                                @RequestBody UserDto userDto) {
        ReloadCommentDto reload = commentService.deleteComment(id, userDto.getUserId());
        if(reload.getParent()) {
            return ResponseEntity.ok(commentService.getParentComments(1, reload.getId()));
        }
        return ResponseEntity.ok(commentService.getChildComments(1, reload.getId()));
    }
}
