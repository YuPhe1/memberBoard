package com.icia.memberboard.service;

import com.icia.memberboard.dto.CommentDTO;
import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.entity.CommentEntity;
import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.repository.BoardRepository;
import com.icia.memberboard.repository.CommentRepository;
import com.icia.memberboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public void save(CommentDTO commentDTO) {
        MemberEntity memberEntity = memberRepository.findById(commentDTO.getMemberId()).orElseThrow(() -> new NoSuchElementException());
        BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId()).orElseThrow(() -> new NoSuchElementException());
        CommentEntity commentEntity = CommentEntity.toSaveCommentEntity(commentDTO, memberEntity, boardEntity);
        commentRepository.save(commentEntity);
    }

    @Transactional
    public List<CommentDTO> findByBoardId(Long boardId) {
        BoardEntity boardEntity = boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException());
        List<CommentEntity> commentEntityList = boardEntity.getCommentEntityList();
        List<CommentDTO> commentDTOList = new ArrayList<>();
        commentEntityList.forEach(commentEntity -> {
            commentDTOList.add(CommentDTO.toCommentDTO(commentEntity));
        });
        return commentDTOList;
    }
}
