package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Dto.BoardDto.BoardDetailDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardListDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardMainDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardRequestDto;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.QBoard;
import com.meonghae.communityservice.Enum.BoardType;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Exception.Error.ErrorCode;
import com.meonghae.communityservice.Repository.BoardRepository;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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


    public Slice<BoardListDto> getBoardList(int typeKey, int page) {
        BoardType type = BoardType.findWithKey(typeKey);
        PageRequest request = PageRequest.of(page - 1, 20,
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Slice<Board> list = boardRepository.findByType(type, request);
        Slice<BoardListDto> listDto = list.map(BoardListDto::new);
        return listDto;
    }

    public BoardDetailDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        BoardDetailDto dto = new BoardDetailDto(board);
        return dto;
    }

    public List<BoardMainDto> getMainBoard() {

        QBoard qBoard = QBoard.board;
        LocalDateTime midnight = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Board> mainBoardLists = jpaQueryFactory.selectFrom(qBoard)
                .where(qBoard.createdDate.between(midnight, now))
                .orderBy(qBoard.type.asc(), qBoard.likes.desc())
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(Board::getType))
                .values()
                .stream()
                .map(boards -> boards.stream()
                        .max(Comparator.comparing(Board::getLikes))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return mainBoardLists.stream().map(BoardMainDto::new).collect(Collectors.toList());
    }

    public void createBoard(int typeKey, BoardRequestDto requestDto) {
        BoardType type = BoardType.findWithKey(typeKey);
        Board board = requestDto.toEntity(type);

        // 임의로 userId 넣음 => 추후에 수정
        board.setOwner(requestDto.getUserId());

        // 이미지 추가하는 부분도 추후에 수정
        boardRepository.save(board);
    }

    public void modifyBoard(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        if(!board.getOwner().equals(requestDto.getUserId())) {
            throw new BoardException(ErrorCode.UNAUTHORIZED, "글 작성자만 수정 가능합니다.");
        }
        board.updateBoard(requestDto.getTitle(), requestDto.getContent());
    }

    public void deleteBoard(Long id, String userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("board is not exist"));
        if(!board.getOwner().equals(userId)) {
            throw new BoardException(ErrorCode.UNAUTHORIZED, "글 작성자만 삭제 가능합니다.");
        }
        boardRepository.delete(board);
    }
}
