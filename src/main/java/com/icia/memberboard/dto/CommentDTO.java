package com.icia.memberboard.dto;

import com.icia.memberboard.entity.CommentEntity;
import com.icia.memberboard.util.UtilClass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDTO {

    private Long id;
    private String commentWriter;
    private String commentContents;
    private Long memberId;
    private Long boardId;
    private String createdAt;
    private String updatedAt;

    public static CommentDTO toCommentDTO(CommentEntity commentEntity){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentEntity.getId());
        commentDTO.setCommentWriter(commentEntity.getCommentWriter());
        commentDTO.setCommentContents(commentEntity.getCommentContents());
        commentDTO.setMemberId(commentEntity.getMemberEntity().getId());
        commentDTO.setBoardId(commentEntity.getBoardEntity().getId());
        commentDTO.setCreatedAt(UtilClass.dateTimeFormat(commentEntity.getCreatedAt()));
        commentDTO.setUpdatedAt(UtilClass.dateTimeFormat(commentEntity.getUpdatedAt()));
        return commentDTO;
    }
}
