package com.icia.memberboard.dto;

import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.entity.BoardFileEntity;
import com.icia.memberboard.util.UtilClass;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardDTO {

    private Long id;
    private String boardWriter;
    private String boardTitle;
    private String boardContents;
    private String createdAt;
    private String updatedAt;
    private int boardHits;
    private int fileAttached;
    private List<MultipartFile> boardFile;
    private List<String> originalFileName = new ArrayList<>();
    private List<String> storedFileName = new ArrayList<>();

    public static BoardDTO toDTO(BoardEntity boardEntity){
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setCreatedAt(UtilClass.dateTimeFormat(boardEntity.getCreatedAt()));
        boardDTO.setUpdatedAt(UtilClass.dateTimeFormat(boardEntity.getUpdatedAt()));

        // 파일 첨부 여부에 따라 파일이름 가져가기
        if(boardEntity.getFileAttached() == 1){
            boardDTO.setFileAttached(1);
            for(BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()){
                boardDTO.getOriginalFileName().add(boardFileEntity.getOriginalFileName());
                boardDTO.getStoredFileName().add(boardFileEntity.getStoredFileName());
            }
        } else {
            boardDTO.setFileAttached(0);
        }
        return boardDTO;
    }
}
