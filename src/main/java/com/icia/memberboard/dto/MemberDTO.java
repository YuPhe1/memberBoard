package com.icia.memberboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class MemberDTO {

    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberName;
    private String memberMobile;
    private String memberBirth;
    private String memberProfile;
    private String createdAt;
    private MultipartFile profile;

}
