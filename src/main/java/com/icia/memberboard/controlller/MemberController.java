package com.icia.memberboard.controlller;

import com.icia.memberboard.dto.MemberDTO;
import com.icia.memberboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/save")
    public String savePage(){
        return "memberPages/memberSave";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO) throws IOException {
        memberService.save(memberDTO);
        return "redirect:/";
    }

    @PostMapping("/dup-check")
    public ResponseEntity dupCheck(@RequestBody MemberDTO memberDTO) {
        boolean result = memberService.emailCheck(memberDTO.getMemberEmail());
        if(result) {
            return new ResponseEntity("사용가능", HttpStatus.OK);
        }else {
            return new ResponseEntity("사용불가능", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirectURI", defaultValue = "/member/mypage") String redirectURI,
                            Model model){
        model.addAttribute("redirectURI", redirectURI);
        return "memberPages/memberLogin";
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody MemberDTO memberDTO, HttpSession session){
        try {
            MemberDTO dto = memberService.findByMemberEmail(memberDTO.getMemberEmail());
            if(dto.getMemberEmail().equals(memberDTO.getMemberEmail())
                    && dto.getMemberPassword().equals(memberDTO.getMemberPassword())){
                session.setAttribute("loginEmail", dto.getMemberEmail());
                session.setAttribute("loginId", dto.getId());
                session.setAttribute("loginName", dto.getMemberName());
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
}
