package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.*;
import com.meonghae.communityservice.Service.BoardCommentService;
import com.meonghae.communityservice.Service.BoardLikeService;
import com.meonghae.communityservice.Service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardCommentService commentService;
    private final BoardLikeService likeService;
    @GetMapping("")
    public ResponseEntity<?> getBoardList(
            @RequestParam(required = false, defaultValue = "1", value = "type") int type) {
        List<BoardListDto> listDto = boardService.getBoardList(type);
        return ResponseEntity.ok(listDto);
    }
    @GetMapping("/{id}")
    public BoardDetailDto getBoard(@PathVariable(name = "id") Long id) {
        return boardService.getBoard(id);
    }

    // 추후에 pathVariable userId 추가하기
    @PostMapping("/{type}")
    public ResponseEntity<String> createBoard(@PathVariable(name = "type") int type,
                                              @RequestBody BoardRequestDto requestDto) {
        boardService.createBoard(type, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 생성 완료");
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<String> createComment(@PathVariable(name = "id") Long id,
                                                @RequestBody CommentRequestDto requestDto) {
        String result = commentService.addComment(id, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> addLike(@PathVariable(name = "id") Long id,
                                          @RequestBody UserDto userDto) {
        String result = likeService.addLike(id, userDto.getUserId());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modifyBoard(@PathVariable(name = "id") Long id,
                                              @RequestBody BoardRequestDto requestDto) {
        boardService.modifyBoard(id, requestDto);
        return ResponseEntity.ok("수정 완료");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable(name = "id") Long id,
                                              @RequestBody UserDto userDto) {
        boardService.deleteBoard(id, userDto.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }
}
