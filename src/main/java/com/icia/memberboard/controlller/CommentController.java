package com.icia.memberboard.controlller;

import com.icia.memberboard.dto.CommentDTO;
import com.icia.memberboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity save(@RequestBody CommentDTO commentDTO){
        try {
            commentService.save(commentDTO);
            List<CommentDTO> commentDTOList = commentService.findByBoardId(commentDTO.getBoardId());
            return new ResponseEntity(commentDTOList, HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@RequestBody CommentDTO commentDTO){
        try {
            commentService.deleteById(commentDTO.getId());
            List<CommentDTO> commentDTOList = commentService.findByBoardId(commentDTO.getBoardId());
            return new ResponseEntity(commentDTOList, HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }
}
