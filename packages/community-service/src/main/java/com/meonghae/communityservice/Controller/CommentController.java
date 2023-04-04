package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.CommentUpdateDto;
import com.meonghae.communityservice.Dto.UserDto;
import com.meonghae.communityservice.Service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final BoardCommentService commentService;
    @PutMapping("/{id}")
    public ResponseEntity<String> modifyingComment(@PathVariable(name = "id") Long id,
                                                    @RequestBody CommentUpdateDto updateDto) {
        commentService.updateComment(id, updateDto);
        return ResponseEntity.ok("수정 완료");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable(name = "id") Long id,
                                                @RequestBody UserDto userDto) {
        commentService.deleteComment(id, userDto.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }
}
