package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.CommentDto.*;
import com.meonghae.communityservice.Dto.UserDto;
import com.meonghae.communityservice.Service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final BoardCommentService commentService;

    @GetMapping("/boards/{id}")
    public List<CommentParentDto> getParentComments(@PathVariable(name = "id") Long id) {
        return commentService.getParentComments(id);
    }
    @GetMapping("/{id}/reply")
    public List<CommentChildDto> getChildComments(@PathVariable(name = "id") Long parentId) {
        return commentService.getChildComments(parentId);
    }

    @PostMapping("/boards/{id}")
    public ResponseEntity<List<CommentParentDto>> createComment(@PathVariable(name = "id") Long id,
                                                                @RequestBody CommentRequestDto requestDto) {
        commentService.addParentComment(id, requestDto);
        List<CommentParentDto> result = commentService.getParentComments(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @PostMapping("/{id}/reply")
    public ResponseEntity<List<CommentChildDto>> addChildComment(@PathVariable(name = "id") Long parentId,
                                  @RequestBody CommentRequestDto requestDto) {
        commentService.addChildComment(parentId, requestDto);
        List<CommentChildDto> result = commentService.getChildComments(parentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<List<?>> modifyingComment(@PathVariable(name = "id") Long id,
                                                    @RequestBody CommentUpdateDto updateDto) {
        ReloadCommentDto reload = commentService.updateComment(id, updateDto);
        if(reload.getParent()) {
            return ResponseEntity.ok(commentService.getParentComments(reload.getId()));
        }
        return ResponseEntity.ok(commentService.getChildComments(reload.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<?>> deleteComment(@PathVariable(name = "id") Long id,
                                                @RequestBody UserDto userDto) {
        ReloadCommentDto reload = commentService.deleteComment(id, userDto.getUserId());
        if(reload.getParent()) {
            return ResponseEntity.ok(commentService.getParentComments(reload.getId()));
        }
        return ResponseEntity.ok(commentService.getChildComments(reload.getId()));
    }
}