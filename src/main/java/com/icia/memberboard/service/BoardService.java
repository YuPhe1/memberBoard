package com.icia.memberboard.service;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.entity.BoardFileEntity;
import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.repository.BoardFileRepository;
import com.icia.memberboard.repository.BoardRepository;
import com.icia.memberboard.repository.MemberRepository;
import com.icia.memberboard.util.UtilClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public Long save(BoardDTO boardDTO) {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(boardDTO.getBoardWriter()).orElseThrow(() -> new NoSuchElementException());
        if (boardDTO.getBoardFile().get(0).isEmpty()) {
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO, memberEntity);
            return boardRepository.save(boardEntity).getId();
        } else {
            BoardEntity boardEntity = BoardEntity.toSaveEntityWithFile(boardDTO, memberEntity);
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
                BoardFileEntity boardFileEntity = BoardFileEntity.toSaveEntity(savedEntity, originalFileName, storedFileName);
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
                        .createdAt(UtilClass.dateFormat((boardEntity.getCreatedAt())))
                        .build());

        return boardList;
    }

    @Transactional
    public void increaseHits(Long id) {
        boardRepository.increaseHist(id);
    }
    @Transactional
    public BoardDTO findById(Long id) {
        return BoardDTO.toDTO(boardRepository.findById(id).orElseThrow(()->new NoSuchElementException()));
    }

    @Transactional
    public void deleteById(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(()->new NoSuchElementException());
        List<BoardFileEntity> boardFileEntityList = boardEntity.getBoardFileEntityList();
        boardFileEntityList.forEach(boardFileEntity -> {
            File file = new File("D:\\springboot_img\\board\\" + boardFileEntity.getStoredFileName());
            if(file.exists()){
                file.delete();
            }
        });
        boardRepository.deleteById(id);
    }

    @Transactional
    public void update(BoardDTO boardDTO, List<String> deleteFileList) throws IOException {
        BoardEntity boardEntity = boardRepository.findById(boardDTO.getId()).orElseThrow(() -> new NoSuchElementException());
        List<BoardFileEntity> boardFileEntityList = boardEntity.getBoardFileEntityList();
        if(deleteFileList != null){
            if(deleteFileList.size() == boardFileEntityList.size() && boardDTO.getBoardFile().get(0).isEmpty()){
                boardDTO.setFileAttached(0);
            } else {
                boardDTO.setFileAttached(1);
            }
            deleteFileList.forEach(deleteFile -> {
                File file = new File("D:\\springboot_img\\board\\"+ deleteFile);
                if(file.exists()){
                    file.delete();
                }
                boardFileRepository.deleteByStoredFileName(deleteFile);
            });
        }
        if(!boardDTO.getBoardFile().get(0).isEmpty()){
            boardDTO.setFileAttached(1);
            List<MultipartFile> boardFileList = boardDTO.getBoardFile();
            for(MultipartFile boardFile : boardFileList){
                String originalFileName = boardFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
                String savePath = "D:\\springboot_img\\board\\";
                boardFile.transferTo(new File(savePath+storedFileName));
                BoardFileEntity boardFileEntity = BoardFileEntity.toSaveEntity(boardEntity, originalFileName, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }
        }
        MemberEntity memberEntity = memberRepository.findByMemberEmail(boardDTO.getBoardWriter()).orElseThrow(() -> new NoSuchElementException());
        BoardEntity updateEntity = BoardEntity.toBoardEntity(boardDTO, memberEntity);
        boardRepository.save(updateEntity);
    }
}
