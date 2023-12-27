package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.S3ServiceClient;
import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.BoardDto.*;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Dto.S3Dto.S3UpdateDto;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.QBoard;
import com.meonghae.communityservice.Enum.BoardType;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Repository.BoardRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.meonghae.communityservice.Exception.Error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardLikeService likeService;
    private final JPAQueryFactory jpaQueryFactory;
    private final RedisService redisService;
    private final UserServiceClient userService;
    private final S3ServiceClient s3Service;

    @Transactional
    public Slice<BoardListDto> getBoardList(int typeKey, int page) {
        BoardType type = BoardType.findWithKey(typeKey);
        PageRequest request = PageRequest.of(page - 1, 20,
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Slice<Board> list = boardRepository.findByType(type, request);
        Slice<BoardListDto> listDto = list.map(board -> {
            String url = redisService.getProfileImage(board.getEmail());
            return new BoardListDto(board, url);
        });
        return listDto;
    }

    @Transactional
    public BoardDetailDto getBoard(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        boolean likeStatus = likeService.isLikeBoard(board, token);
        boolean isWriter = Objects.equals(board.getEmail(), userService.getUserEmail(token));
        String url = redisService.getProfileImage(board.getEmail());
        BoardDetailDto detailDto = new BoardDetailDto(board, url, likeStatus, isWriter);
        if (board.getHasImage()) {
            List<S3ResponseDto> images = s3Service.getImages(new S3RequestDto(board.getId(), "BOARD"));
            detailDto.setImages(images);
        }
        return detailDto;
    }

    @Transactional
    public List<BoardMainDto> getMainBoard() {

        List<Board> mainBoardLists;

        QBoard qBoard = QBoard.board;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1).toLocalDate().atStartOfDay();

        mainBoardLists = Arrays.stream(BoardType.values()).map(type -> jpaQueryFactory.selectFrom(qBoard)
                .where(qBoard.type.eq(type), qBoard.createdDate.between(yesterday, now))
                .orderBy(qBoard.likes.desc(), qBoard.createdDate.desc())
                .limit(1)
                .fetchOne()).filter(Objects::nonNull).collect(Collectors.toList());

        return mainBoardLists.stream().map(BoardMainDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void createBoard(int typeKey, BoardRequestDto requestDto, String token) {
        BoardType type = BoardType.findWithKey(typeKey);
        String email = userService.getUserEmail(token);
        Board board = requestDto.toEntity(type, email);
        Board saveBoard = boardRepository.save(board);
        List<MultipartFile> images = requestDto.getImages();
        if (images != null) {
            imageCheck(saveBoard, images, 0);
            S3RequestDto s3Dto = new S3RequestDto(saveBoard.getId(), "BOARD");
            s3Service.uploadImage(images, s3Dto);
            saveBoard.toggleHasImage();
        }
    }

    // 1. 새로운 이미지 없음
    //  1-1. 기존 이미지 삭제 -> 재사용 이미지 없으면 전체삭제 or 있으면 수정 작업
    //  1-2. 기존 이미지 유지 -> 따로 작업 필요 없음

    // 2. 새로운 이미지 추가
    //  2-1. 기존 이미지 없음 => 그냥 추가작업 진행
    //  2-2. 기존 이미지 있음 => 수정 작업 진행
    @Transactional
    public void modifyBoard(Long id, BoardUpdateDto updateDto, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        String email = userService.getUserEmail(token);
        if (!board.getEmail().equals(email)) {
            throw new BoardException(UNAUTHORIZED, "글 작성자만 수정 가능합니다.");
        }
        board.updateBoard(updateDto.getTitle(), updateDto.getContent());

        int reuseSize = 0;
        List<S3UpdateDto> deletedDto = new ArrayList<>(); // 삭제되는 기존 이미지 저장
        if (!CollectionUtils.isEmpty(updateDto.getUpdateDto())) { // 재사용되는 이미지 사이즈 구하기
            deletedDto = updateDto.getUpdateDto()
                    .stream().filter(dto -> dto.isDeleted()).collect(Collectors.toList());

            // 재사용 이미지 = 기존 전체 이미지 - 삭제되는 이미지
            reuseSize = updateDto.getUpdateDto().size() - deletedDto.size();
        }

        List<MultipartFile> images = updateDto.getImages();
        if (CollectionUtils.isEmpty(images)) { // 새로운 이미지 없음
            if (!CollectionUtils.isEmpty(deletedDto)) { // 삭제할 이미지 존재
                if (reuseSize == 0) { // 재사용 이미지 없으면 전체 이미지 삭제
                    s3Service.deleteImage(new S3RequestDto(board.getId(), "BOARD"));
                    board.toggleHasImage();
                } else { // 재사용 이미지 있으면 업데이트 진행
                    s3Service.updateImage(updateDto.getImages(), updateDto.getUpdateDto());
                }
            }
        } else { // 새로운 이미지 추가
            imageCheck(board, images, reuseSize);

            // 기존 이미지 없었음 => 새로운 이미지 추가만 진행
            if (CollectionUtils.isEmpty(updateDto.getUpdateDto())) {
                s3Service.uploadImage(images, new S3RequestDto(board.getId(), "BOARD"));
                board.toggleHasImage();
            } else { // 기존 이미지 존재 => 이미지 업데이트 진행
                s3Service.updateImage(images, updateDto.getUpdateDto());
            }
        }
    }

    @Transactional
    public void deleteBoard(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(NOT_FOUND, "board is not exist"));
        String email = userService.getUserEmail(token);
        if (!board.getEmail().equals(email)) {
            throw new BoardException(UNAUTHORIZED, "글 작성자만 삭제 가능합니다.");
        }
        S3RequestDto requestDto = new S3RequestDto(board.getId(), "BOARD");
        s3Service.deleteImage(requestDto);
        boardRepository.delete(board);
    }

    @Transactional
    public void deleteBoardByEmail(String email) {
        boardRepository.deleteAllByEmail(email);
    }

    public void imageCheck(Board saveBoard, List<MultipartFile> images, int reuseSize) {
        if (saveBoard.getType() == BoardType.MISSING && images.size() - reuseSize > 5) {
            throw new BoardException(BAD_REQUEST, "실종 게시글 사진은 최대 5개까지 업로드 가능합니다.");
        } else if (images.size() - reuseSize > 3) {
            throw new BoardException(BAD_REQUEST, "게시글 사진은 최대 3개까지 업로드 가능합니다.");
        }
    }
}
