package com.icia.memberboard.dto;

import com.icia.memberboard.entity.BoardFileEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardFileDTO {
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private Long boardId;

    public static BoardFileDTO toBoardFileDTO(BoardFileEntity boardFileEntity){
        BoardFileDTO boardFileDTO = new BoardFileDTO();
        boardFileDTO.setId(boardFileEntity.getId());
        boardFileDTO.setOriginalFileName(boardFileEntity.getOriginalFileName());
        boardFileDTO.setStoredFileName(boardFileEntity.getStoredFileName());
        boardFileDTO.setBoardId(boardFileDTO.getBoardId());
        return boardFileDTO;
    }
}
