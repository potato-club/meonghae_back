package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Dto.BoardDto.BoardDetailDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardListDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardMainDto;
import com.meonghae.communityservice.Dto.BoardDto.BoardRequestDto;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Exception.Error.ErrorCode;
import com.meonghae.communityservice.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public Slice<BoardListDto> getBoardList(int typeKey, int page) {
        BoardType type = BoardType.findWithKey(typeKey);
        PageRequest request = PageRequest.of(page - 1, 20,
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Slice<Board> list = boardRepository.findByType(type, request);
        Slice<BoardListDto> listDto = list.map(BoardListDto::new);
        return listDto;
    }

    @Transactional
    public BoardDetailDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        BoardDetailDto dto = new BoardDetailDto(board);
        return dto;
    }

    @Transactional
    public List<BoardMainDto> getMainBoard() {
        List<Board> mainBoardList = boardRepository.findTop1LikedPerType();
        return mainBoardList.stream().map(BoardMainDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void createBoard(int typeKey, BoardRequestDto requestDto) {
        BoardType type = BoardType.findWithKey(typeKey);
        Board board = requestDto.toEntity(type);

        // 임의로 userId 넣음 => 추후에 수정
        board.setOwner(requestDto.getUserId());

        // 이미지 추가하는 부분도 추후에 수정
        boardRepository.save(board);
    }

    @Transactional
    public void modifyBoard(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BAD_REQUEST, "board is not exist"));
        if(!board.getOwner().equals(requestDto.getUserId())) {
            throw new BoardException(ErrorCode.UNAUTHORIZED, "글 작성자만 수정 가능합니다.");
        }
        board.updateBoard(requestDto.getTitle(), requestDto.getContent());
    }

    @Transactional
    public void deleteBoard(Long id, String userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("board is not exist"));
        if(!board.getOwner().equals(userId)) {
            throw new BoardException(ErrorCode.UNAUTHORIZED, "글 작성자만 삭제 가능합니다.");
        }
        boardRepository.delete(board);
    }
}
