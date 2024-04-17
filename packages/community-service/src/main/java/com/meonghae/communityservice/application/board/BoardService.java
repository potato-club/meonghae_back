package com.meonghae.communityservice.application.board;

import com.meonghae.communityservice.application.board.port.BoardLikeRepository;
import com.meonghae.communityservice.application.board.port.CommentRepository;
import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.board.port.BoardRepository;
import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardLike;
import com.meonghae.communityservice.dto.board.*;
import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.dto.s3.S3Update;
import com.meonghae.communityservice.domain.board.BoardType;
import com.meonghae.communityservice.exception.custom.BoardException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.meonghae.communityservice.exception.error.ErrorCode.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@Builder
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final RedisPort redisService;
    private final UserServicePort userService;
    private final S3ServicePort s3Service;

    public Slice<BoardList> getBoardList(int typeKey, int page) {
        BoardType type = BoardType.findWithKey(typeKey);
        PageRequest request = PageRequest.of(page - 1, 20,
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Slice<Board> list = boardRepository.findByType(type, request);

        List<Long> boardIds = list.getContent().stream().map(Board::getId).collect(Collectors.toList());

        Map<Long, Long> commentCount = commentRepository.findCommentCountByBoardIds(boardIds);

        return list.map(board -> {
            String url = redisService.getProfileImage(board.getEmail());
            int commentSize = commentCount.getOrDefault(board.getId(), 0L).intValue();
            return new BoardList(board, url, commentSize);
        });
    }

    public BoardDetail getBoard(Long id, String token) {
        String userEmail = userService.getUserEmail(token);
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));

        BoardLike like = likeRepository.findByEmailAndBoardEntity_Id(userEmail, id);
        int commentCount = commentRepository.countByBoardId(board.getId());

        boolean likeStatus = like != null ? like.getStatus() : false;
        boolean isWriter = Objects.equals(board.getEmail(), userEmail);
        String url = redisService.getProfileImage(board.getEmail());
        BoardDetail detailDto = new BoardDetail(board, url, likeStatus, isWriter, commentCount);

        if (board.getHasImage()) {
            List<S3Response> images = s3Service.getImages(new S3Request(board.getId(), "BOARD"));
            detailDto.setImages(images);
        }

        return detailDto;
    }

    public List<BoardMain> getMainBoard() {
        LocalDateTime now = LocalDateTime.now();
        List<Board> boardList = boardRepository.findBoardListForMain(now);

        Map<Long, Long> commentCount = commentRepository.findCommentCountByBoardIds(boardList.stream()
                .map(Board::getId).collect(Collectors.toList()));

        return boardList.stream().map(board -> new BoardMain(board,
                        commentCount.getOrDefault(board.getId(), 0L).intValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Board createBoard(int typeKey, BoardRequest requestDto, String token) {
        BoardType type = BoardType.findWithKey(typeKey);
        String email = userService.getUserEmail(token);

        Board board = Board.create(requestDto, type, email);
        Board savedBoard = boardRepository.save(board);

        List<MultipartFile> images = requestDto.getImages();
        if (!CollectionUtils.isEmpty(images)) {
            imageCheck(savedBoard, images);
            S3Request s3Dto = new S3Request(savedBoard.getId(), "BOARD");
            s3Service.uploadImage(images, s3Dto);
            savedBoard.toggleHasImage();

            savedBoard = boardRepository.save(savedBoard);
        }
        return savedBoard;
    }

    // 기존 이미지 없음
    // 1. 새로운 이미지 있음? -> 업로드
    // 2. 새로운 이미지 없음? -> 스킵

    // 기존 이미지 있음
    // 1. 새로운 이미지 있음? -> 업데이트
    // 2. 새로운 이미지 없음? -> 기존 이미지 삭제
    @Transactional
    public Board modifyBoard(Long id, BoardUpdate updateDto, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        String email = userService.getUserEmail(token);
        if (!board.getEmail().equals(email)) {
            throw new BoardException(UNAUTHORIZED, "글 작성자만 수정 가능합니다.");
        }
        Board updateBoard = board.updateBoard(updateDto.getTitle(), updateDto.getContent());

        S3Request requestDto = new S3Request(board.getId(), "BOARD");
        List<MultipartFile> images = updateDto.getImages(); // 새롭게 저장할 이미지 데이터

        // 기존 이미지 없음 & 새로운 이미지 있음 -> 업로드 + 이미지 상태변경
        if (!updateBoard.getHasImage() && !CollectionUtils.isEmpty(images)) {
            imageCheck(updateBoard, images); // 새로운 이미지 검증
            s3Service.uploadImage(images, requestDto);
            updateBoard.toggleHasImage();
            return boardRepository.save(updateBoard);
        }

        // 기존 이미지 있음
        if (updateBoard.getHasImage()) {
            List<S3Response> response = s3Service.getImages(requestDto);
            List<S3Update> originImages = response.stream().map(S3Update::new).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(images)) { // 새로운 이미지 있음 -> 업데이트
                imageCheck(updateBoard, images); // 새로운 이미지 검증
                s3Service.updateImage(images, originImages);
            } else { // 새로운 이미지 없음 -> 이미지 전체 삭제 + 이미지 상태 변경
                s3Service.deleteImage(requestDto);
                updateBoard.toggleHasImage();
                return boardRepository.save(updateBoard);
            }
        }
        return boardRepository.save(updateBoard);
    }

    @Transactional
    public void deleteBoard(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(NOT_FOUND, "board is not exist"));
        String email = userService.getUserEmail(token);
        if (!board.getEmail().equals(email)) {
            throw new BoardException(UNAUTHORIZED, "글 작성자만 삭제 가능합니다.");
        }
        S3Request requestDto = new S3Request(board.getId(), "BOARD");
        s3Service.deleteImage(requestDto);
        boardRepository.delete(id);
    }

    private void imageCheck(Board savedBoard, List<MultipartFile> images) {
        if (savedBoard.getType() == BoardType.MISSING && images.size() > 5) {
            throw new BoardException(BAD_REQUEST, "실종 게시글 사진은 최대 5개까지 업로드 가능합니다.");
        } else if (images.size() > 3) {
            throw new BoardException(BAD_REQUEST, "게시글 사진은 최대 3개까지 업로드 가능합니다.");
        }
    }
}
