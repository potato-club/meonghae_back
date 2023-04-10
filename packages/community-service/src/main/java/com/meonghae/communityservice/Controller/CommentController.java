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
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final BoardCommentService commentService;

    @GetMapping("/boards/{id}")
    public Slice<CommentParentDto> getParentComments(@PathVariable(name = "id") Long id,
                                                     @RequestParam(required = false,
                                                            defaultValue = "1", value = "page") int page) {
        return commentService.getParentComments(page, id);
    }
    @GetMapping("/{id}/reply")
    public Slice<CommentChildDto> getChildComments(@PathVariable(name = "id") Long parentId,
                                                  @RequestParam(required = false,
                                                          defaultValue = "1", value = "page") int page) {
        return commentService.getChildComments(page, parentId);
    }

    @PostMapping("/boards/{id}")
    public ResponseEntity<Slice<CommentParentDto>> createComment(@PathVariable(name = "id") Long id,
                                                                @RequestBody CommentRequestDto requestDto) {
        commentService.addParentComment(id, requestDto);
        Slice<CommentParentDto> result = commentService.getParentComments(1, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @PostMapping("/{id}/reply")
    public ResponseEntity<Slice<CommentChildDto>> addChildComment(@PathVariable(name = "id") Long parentId,
                                  @RequestBody CommentRequestDto requestDto) {
        commentService.addChildComment(parentId, requestDto);
        Slice<CommentChildDto> result = commentService.getChildComments(1, parentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Slice<?>> modifyingComment(@PathVariable(name = "id") Long id,
                                                    @RequestBody CommentUpdateDto updateDto) {
        ReloadCommentDto reload = commentService.updateComment(id, updateDto);
        if(reload.getParent()) {
            return ResponseEntity.ok(commentService.getParentComments(1, reload.getId()));
        }
        return ResponseEntity.ok(commentService.getChildComments(1, reload.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Slice<?>> deleteComment(@PathVariable(name = "id") Long id,
                                                @RequestBody UserDto userDto) {
        ReloadCommentDto reload = commentService.deleteComment(id, userDto.getUserId());
        if(reload.getParent()) {
            return ResponseEntity.ok(commentService.getParentComments(1, reload.getId()));
        }
        return ResponseEntity.ok(commentService.getChildComments(1, reload.getId()));
    }
}
