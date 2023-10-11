package com.icia.memberboard.controlller;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String findAll(Model model,
                          @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                          @RequestParam(value = "type", required = false, defaultValue = "boardTitle") String type,
                          @RequestParam(value = "q", required = false, defaultValue = "") String q) {
        Page<BoardDTO> boardDTOList = boardService.findAll(page, type, q);
        // 목록 하단에 보여줄 페이지 번호
        int blockLimit = 3;
        int startPage = (((int) (Math.ceil((double) page / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < boardDTOList.getTotalPages()) ? startPage + blockLimit - 1 : boardDTOList.getTotalPages();
        model.addAttribute("boardList", boardDTOList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("page", page);
        model.addAttribute("type", type);
        model.addAttribute("q", q);
        return "boardPages/boardList";
    }

    @GetMapping("/save")
    public String savePage() {
        return "boardPages/boardSave";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) {
        boardService.save(boardDTO);
        return "redirect:/board";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Long id,
                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                           @RequestParam(value = "type", required = false, defaultValue = "boardTitle") String type,
                           @RequestParam(value = "q", required = false, defaultValue = "") String q,
                           HttpServletRequest request, HttpServletResponse response,
                           Model model) {
        Cookie[] cookies = request.getCookies();
        boolean isHit = false;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("hit" + id)) {
                isHit = true;
            }
        }
        if (!isHit) {
            boardService.increaseHits(id);
            Cookie cookie = new Cookie("hit" + id, "1");
            cookie.setPath("/");
            cookie.setMaxAge(5 * 60);
            response.addCookie(cookie);
        }
        try {
            BoardDTO boardDTO = boardService.findById(id);
            model.addAttribute("board", boardDTO);
            model.addAttribute("page", page);
            model.addAttribute("type", type);
            model.addAttribute("q", q);
            return "boardPages/boardDetail";
        } catch (NoSuchElementException e) {
            return "boardPages/boardNotFound";
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        boardService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable("id") Long id, Model model, HttpSession session) {
        BoardDTO boardDTO = boardService.findById(id);
        if (boardDTO.getBoardWriter().equals((String) session.getAttribute("loginEmail"))) {
            model.addAttribute("board", boardDTO);
            return "boardPages/boardUpdate";
        } else {
            model.addAttribute("redirectURI", "/board/" + id);
            return "boardPages/boardNoAuthority";
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, @RequestParam(name = "deleteFile", required = false) List<String> deleteFileList) throws IOException {
        boardService.update(boardDTO, deleteFileList);
        return "redirect:/board/" + boardDTO.getId();
    }
}
