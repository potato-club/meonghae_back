package com.meonghae.communityservice.web.board.board;

import com.meonghae.communityservice.dto.board.*;
import com.meonghae.communityservice.application.board.BoardLikeService;
import com.meonghae.communityservice.application.board.BoardService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
@Api(value = "BOARD_CONTROLLER", tags = "게시판 서비스 컨트롤러")
public class BoardController {
    private final BoardService boardService;
    private final BoardLikeService likeService;

    @Operation(summary = "게시글 리스트 호출 API")
    @GetMapping("")
    public ResponseEntity<Slice<BoardList>> getBoardList(
            @RequestParam(required = false, defaultValue = "1", value = "type") int type,
            @RequestParam(required = false, defaultValue = "1", value = "p") int page) {
        Slice<BoardList> listDto = boardService.getBoardList(type, page);
        return ResponseEntity.ok(listDto);
    }

    @Operation(summary = "특정 게시글 호출 API")
    @GetMapping("/{id}")
    public BoardDetail getBoard(@PathVariable(name = "id") Long id,
                                @RequestHeader("Authorization") String token) {
        return boardService.getBoard(id, token);
    }

    @Operation(summary = "메인 페이지 인기게시글 호출 API")
    @GetMapping("/main")
    public ResponseEntity<List<BoardMain>> getMainBoardList() {
        List<BoardMain> mainDto = boardService.getMainBoard();
        return ResponseEntity.ok(mainDto);
    }

    @Operation(summary = "게시글 생성 API")
    @PostMapping("/{type}")
    public ResponseEntity<String> createBoard(@PathVariable(name = "type") int type,
                                              @RequestHeader("Authorization") String token,
                                              BoardRequest requestDto) {
        boardService.createBoard(type, requestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 생성 완료");
    }

    @Operation(summary = "게시글 좋아요 기능 API")
    @PostMapping("/{id}/like")
    public ResponseEntity<String> addLike(@PathVariable(name = "id") Long id,
                                          @RequestHeader("Authorization") String token) {
        String result = likeService.addLike(id, token);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시글 수정 API")
    @PutMapping("/{id}")
    public ResponseEntity<String> modifyBoard(@PathVariable(name = "id") Long id,
                                              @RequestHeader("Authorization") String token,
                                              BoardUpdate updateDto) {
        boardService.modifyBoard(id, updateDto, token);
        return ResponseEntity.ok("수정 완료");
    }

    @Operation(summary = "게시글 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable(name = "id") Long id,
                                              @RequestHeader("Authorization") String token) {
        boardService.deleteBoard(id, token);
        return ResponseEntity.ok("삭제 완료");
    }

    @Operation(summary = "유저의 게시글 삭제 API")
    @DeleteMapping("/users")
    public String deleteUserBoard(@RequestParam(name = "email") String email) {
        boardService.deleteBoardByEmail(email);
        return "삭제 완료";
    }
}
