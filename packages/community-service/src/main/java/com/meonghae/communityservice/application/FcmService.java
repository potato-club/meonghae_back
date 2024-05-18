package com.meonghae.communityservice.application;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.dto.comment.CommentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FcmService {
    public void pushMessage(Board board, CommentRequest dto) {

    }
}
