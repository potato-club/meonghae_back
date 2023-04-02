package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Dto.BoardDetailDto;
import com.meonghae.communityservice.Dto.BoardListDto;
import com.meonghae.communityservice.Dto.BoardRequestDto;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import com.meonghae.communityservice.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<BoardListDto> getBoardList(int typeKey) {
        BoardType type = BoardType.findWithKey(typeKey);
        List<Board> list = boardRepository.findByType(type);
        List<BoardListDto> listDto = list.stream().map(BoardListDto::new).collect(Collectors.toList());
        return listDto;
    }

    public BoardDetailDto getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("board is not exist"));
        BoardDetailDto dto = new BoardDetailDto(board);
        return dto;
    }

    public void createBoard(int typeKey, BoardRequestDto requestDto) {
        BoardType type = BoardType.findWithKey(typeKey);
        Board board = requestDto.toEntity(type);

        // 임의로 userId 넣음 => 추후에 수정
        board.setOwner(requestDto.getUserId());

        // 이미지 추가하는 부분도 추후에 수정
        boardRepository.save(board);
    }
}
