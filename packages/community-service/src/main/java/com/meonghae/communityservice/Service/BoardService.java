package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.S3ServiceClient;
import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.BoardDto.BoardDetailDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardListDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardMainDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardRequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.QBoard;
import com.meonghae.communityservice.Enum.BoardType;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Exception.Error.ErrorCode;
import com.meonghae.communityservice.Repository.BoardRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
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
            String nickname = redisService.getNickname(board.getEmail());
            return new BoardListDto(board, nickname);
        });
        return listDto;
    }

    @Transactional
    public BoardDetailDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        String nickname = redisService.getNickname(board.getEmail());
        BoardDetailDto detailDto = new BoardDetailDto(board, nickname);
        if(board.getHasImage()) {
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

        return mainBoardLists.stream().map(board -> {
            String nickname = redisService.getNickname(board.getEmail());
            return new BoardMainDto(board, nickname);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void createBoard(int typeKey, List<MultipartFile> images, BoardRequestDto requestDto, String token) {
        BoardType type = BoardType.findWithKey(typeKey);
        String email = userService.getUserEmail(token);
        Board board = requestDto.toEntity(type, email);

        Board saveBoard = boardRepository.save(board);
        if(images != null) {
            S3RequestDto s3Dto = new S3RequestDto(saveBoard.getId(), "BOARD");
            s3Service.uploadImage(images, s3Dto);
            saveBoard.setHasImage();
        }
    }

    // 이미지 업로드 수정시 변경 로직 추가
    @Transactional
    public void modifyBoard(Long id, BoardRequestDto requestDto, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        String email = userService.getUserEmail(token);
        if(!board.getEmail().equals(email)) {
            throw new BoardException(ErrorCode.UNAUTHORIZED, "글 작성자만 수정 가능합니다.");
        }
        board.updateBoard(requestDto.getTitle(), requestDto.getContent());
    }

    @Transactional
    public void deleteBoard(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("board is not exist"));
        String email = userService.getUserEmail(token);
        if(!board.getEmail().equals(email)) {
            throw new BoardException(ErrorCode.UNAUTHORIZED, "글 작성자만 삭제 가능합니다.");
        }
        boardRepository.delete(board);
    }
}
