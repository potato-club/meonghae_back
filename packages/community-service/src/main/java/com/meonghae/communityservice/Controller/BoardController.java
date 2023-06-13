package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.BoardDto.BoardDetailDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardListDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardMainDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardRequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3UpdateDto;
import com.meonghae.communityservice.Service.BoardLikeService;
import com.meonghae.communityservice.Service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Api(value = "BOARD_CONTROLLER", tags = "게시판 서비스 컨트롤러")
public class BoardController {
    private final BoardService boardService;
    private final BoardLikeService likeService;

    @GetMapping("/health")
    public String check() {
        return "hi~";
    }

    @Operation(summary = "게시글 리스트 호출 API")
    @GetMapping("")
    public ResponseEntity<Slice<BoardListDto>> getBoardList(
            @RequestParam(required = false, defaultValue = "1", value = "type") int type,
            @RequestParam(required = false, defaultValue = "1", value = "p") int page) {
        Slice<BoardListDto> listDto = boardService.getBoardList(type, page);
        return ResponseEntity.ok(listDto);
    }

    @Operation(summary = "특정 게시글 호출 API")
    @GetMapping("/{id}")
    public BoardDetailDto getBoard(@PathVariable(name = "id") Long id) {
        return boardService.getBoard(id);
    }

    @Operation(summary = "메인 페이지 인기게시글 호출 API")
    @GetMapping("/main")
    public ResponseEntity<List<BoardMainDto>> getMainBoardList() {
        List<BoardMainDto> mainDto = boardService.getMainBoard();
        return ResponseEntity.ok(mainDto);
    }

    @Operation(summary = "게시글 생성 API")
    @PostMapping("/{type}")
    public ResponseEntity<String> createBoard(@PathVariable(name = "type") int type,
                                              @RequestHeader("Authorization") String token,
                                              BoardRequestDto requestDto) {
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
                                              @RequestPart(required = false) List<MultipartFile> images,
                                              @RequestPart(required = false) List<S3UpdateDto> updateDto,
                                              @RequestPart BoardRequestDto requestDto,
                                              @RequestHeader("Authorization") String token) {
        boardService.modifyBoard(id, images, updateDto, requestDto, token);
        return ResponseEntity.ok("수정 완료");
    }

    @Operation(summary = "게시글 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable(name = "id") Long id,
                                              @RequestHeader("Authorization") String token) {
        boardService.deleteBoard(id, token);
        return ResponseEntity.ok("삭제 완료");
    }
}
