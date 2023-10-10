package com.icia.memberboard.service;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.entity.BoardFileEntity;
import com.icia.memberboard.repository.BoardFileRepository;
import com.icia.memberboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
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
}
