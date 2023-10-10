package com.icia.memberboard.service;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.entity.BoardFileEntity;
import com.icia.memberboard.repository.BoardFileRepository;
import com.icia.memberboard.repository.BoardRepository;
import com.icia.memberboard.util.UtilClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public Long save(BoardDTO boardDTO) {
        if (boardDTO.getBoardFile().get(0).isEmpty()) {
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            return boardRepository.save(boardEntity).getId();
        } else {
            BoardEntity boardEntity = BoardEntity.toSaveEntityWithFile(boardDTO);
            BoardEntity savedEntity = boardRepository.save(boardEntity);
            List<MultipartFile> boardFileList = boardDTO.getBoardFile();
            boardFileList.forEach(multipartFile -> {
                String originalFileName = multipartFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
                String savePath = "D:\\springboot_img\\board\\" + storedFileName;
                try {
                    multipartFile.transferTo((new File(savePath)));
                } catch (IOException e) {
                    System.out.println("게시물 파일 저장 오류");
                    throw new RuntimeException(e);
                }
                BoardFileEntity boardFileEntity = BoardFileEntity.toSaveEntity(boardEntity, originalFileName, storedFileName);
                boardFileRepository.save(boardFileEntity);
            });
            return savedEntity.getId();
        }
    }

    public Page<BoardDTO> findAll(int page, String type, String q) {
        page = page - 1;
        int pageLimit = 5;
        Page<BoardEntity> boardEntities = null;
        // 검색인지 구분
        if (q.equals("")) {
            // 일반 페이징
            boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        } else {
            if (type.equals("boardTitle")) {
                boardEntities = boardRepository.findByBoardTitleContaining(q, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
            } else if (type.equals("boardWriter")) {
                boardEntities = boardRepository.findByBoardWriterContaining(q, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
            }
        }

        Page<BoardDTO> boardList = boardEntities.map(boardEntity ->
                BoardDTO.builder()
                        .id(boardEntity.getId())
                        .boardWriter(boardEntity.getBoardWriter())
                        .boardTitle(boardEntity.getBoardTitle())
                        .boardHits(boardEntity.getBoardHits())
                        .createdAt(UtilClass.dateTimeFormat((boardEntity.getCreatedAt())))
                        .build());

        return boardList;
    }
}
