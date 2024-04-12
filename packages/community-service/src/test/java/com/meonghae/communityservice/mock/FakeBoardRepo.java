package com.meonghae.communityservice.mock;

import com.meonghae.communityservice.application.board.port.BoardRepository;
import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.domain.board.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeBoardRepo implements BoardRepository {

    private final AtomicLong autoGeneratedId = new AtomicLong(0L);
    private final List<Board> data = new ArrayList<>();

    @Override
    public Optional<Board> findById(Long id) {
        return data.stream().filter(board -> board.getId().equals(id)).findAny();
    }

    @Override
    public Slice<Board> findByType(BoardType type, Pageable pageable) {
        return null;
    }

    @Override
    public List<Board> findBoardListForMain(LocalDateTime now) {
        return null;
    }

    @Override
    public void deleteAllByEmail(String email) {
        data.removeIf(board -> Objects.equals(board.getEmail(), email));
    }

    @Override
    public Board save(Board board) {
        LocalDateTime now = LocalDateTime.now();
        if (board.getId() == null || board.getId() == 0) {
            Board saveBoard = Board.builder()
                    .title(board.getTitle())
                    .content(board.getContent())
                    .likes(board.getLikes())
                    .hasImage(board.getHasImage())
                    .createdDate(now)
                    .modifiedDate(now)
                    .email(board.getEmail())
                    .comments(board.getComments())
                    .type(board.getType())
                    .id(autoGeneratedId.incrementAndGet())
                    .build();

            data.add(saveBoard);
            return saveBoard;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), board.getId()));
            data.add(board);
            return board;
        }
    }

    @Override
    public void delete(Board board) {
        data.removeIf(item -> Objects.equals(item.getId(), board.getId()));
    }
}