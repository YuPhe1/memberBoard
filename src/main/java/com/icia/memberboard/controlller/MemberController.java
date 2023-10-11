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
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 페이지
     */
    @GetMapping("/save")
    public String savePage() {
        return "memberPages/memberSave";
    }

    /**
     * 회원가입
     */
    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO) throws IOException {
        memberService.save(memberDTO);
        return "redirect:/member/login";
    }

    /**
     * 이메일 중복 체크
     */
    @PostMapping("/dup-check")
    public ResponseEntity dupCheck(@RequestBody MemberDTO memberDTO) {
        boolean result = memberService.emailCheck(memberDTO.getMemberEmail());
        if (result) {
            return new ResponseEntity("사용가능", HttpStatus.OK);
        } else {
            return new ResponseEntity("사용불가능", HttpStatus.CONFLICT);
        }
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirectURI", defaultValue = "/board") String redirectURI,
                            Model model) {
        model.addAttribute("redirectURI", redirectURI);
        return "memberPages/memberLogin";
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody MemberDTO memberDTO, HttpSession session) {
        try {
            MemberDTO dto = memberService.findByMemberEmail(memberDTO.getMemberEmail());
            if (dto.getMemberEmail().equals(memberDTO.getMemberEmail())
                    && dto.getMemberPassword().equals(memberDTO.getMemberPassword())) {
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

    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /**
     * 회원 정보 조회(login)
     */
    @GetMapping("/myPage")
    public String myPage(HttpSession session, Model model) {
        String loginEmail = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.findByMemberEmail(loginEmail);
        model.addAttribute("member", memberDTO);
        return "memberPages/memberDetail";
    }

    /**
     * 회원 정보 조회(id)
     */
    @GetMapping("/{id}")
    public String myPage(@PathVariable("id") Long id, Model model) {
        try {
            MemberDTO memberDTO = memberService.findById(id);
            model.addAttribute("member", memberDTO);
            return "memberPages/memberDetail";
        } catch (NoSuchElementException e){
            return "memberPages/memberNotFound";
        }
    }


    /**
     * 회원목록 출력
     */
    @GetMapping()
    public String list(Model model){
        model.addAttribute("memberList", memberService.findAll());
        return "memberPages/memberList";
    }

    /**
     * 회원삭제
     * axios
     */
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        memberService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 회원삭제 비밀번호 확인
     */
    @GetMapping("/delete-check")
    public String deleteCheck(Model model, HttpSession session){
        String email = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.findByMemberEmail(email);
        model.addAttribute("member", memberDTO);
        return "memberPages/memberDeleteCheck";
    }

    /**
     * 회원 수정 페이지
     */
    @GetMapping("/update")
    public String updatePage(Model model, HttpSession session){
        String email = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.findByMemberEmail(email);
        model.addAttribute("member", memberDTO);
        return "memberPages/memberUpdate";
    }

    /**
     * 회원수정
     */
    @PostMapping("/update")
    public String update(HttpSession session, @ModelAttribute MemberDTO memberDTO) throws IOException {
        memberService.update(memberDTO);
        session.invalidate();
        return "redirect:/member/login";
    }
}
